package com.example.lifecollage.repository

import android.util.Log
import com.example.lifecollage.model.CollageItem
import com.example.lifecollage.network.RetrofitClient
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import com.google.gson.Gson
import com.google.gson.JsonObject

class CollageRepository(private val realm: Realm) {

    private val api = RetrofitClient.api

    // 1. READ
    fun getAllItems() = realm.query<CollageItem>("isDeleted == false").find()

    // 2. REFRESH (Call this from ViewModel init)
    // 2. REFRESH
    suspend fun refreshItems() {
        withContext(Dispatchers.IO) {
            try {
                val serverItems = api.getAllCollages()
                val serverIds = serverItems.mapNotNull { it.serverId }.toSet()

                realm.write {
                    // 1️⃣ Update / Insert from server
                    serverItems.forEach { serverItem ->
                        // --- FIX: GSON leaves _id null, so we must generate one ---
                        // We assume it's a new object if we are inserting it.
                        // (We don't read _id from serverItem anyway)
                        // ----------------------------------------------------------

                        val localItem = query<CollageItem>("serverId == $0", serverItem.serverId).first().find()

                        if (localItem != null) {
                            // Don't overwrite if we are trying to delete it!
                            if (!localItem.isDeleted) {
                                localItem.apply {
                                    title = serverItem.title
                                    description = serverItem.description
                                    rating = serverItem.rating
                                    date = serverItem.date
                                    imageUri = serverItem.imageUri
                                    isSynced = true
                                    // Don't touch _id here, localItem already has one
                                }
                            }
                        } else {
                            // Insert new
                            // MANUALLY ASSIGN ID to fix the crash
                            serverItem._id = ObjectId()

                            copyToRealm(serverItem.apply {
                                isSynced = true
                                isDeleted = false
                            })
                        }
                    }

                    // 2️⃣ DELETE locals missing from server
                    val localItems = query<CollageItem>("serverId != null").find()
                    localItems.forEach { local ->
                        // Only delete if it's synced (meaning it WAS on server, but now isn't)
                        // And verify it's not currently marked for deletion (optional, but safe)
                        if (!serverIds.contains(local.serverId) && local.isSynced) {
                            delete(local)
                        }
                    }
                }
                Log.d("Repo", "Refresh successful") // Log success
            } catch (e: Exception) {
                // LOG THE REAL ERROR so we can see it!
                Log.e("Repo", "Refresh failed", e)
            }
        }
    }


    // 3. CREATE
    suspend fun addItem(title: String, description: String, rating: String, date: String, imageUri: String?) {
        withContext(Dispatchers.IO) {
            val newItem = CollageItem().apply {
                this.title = title
                this.description = description
                this.rating = rating
                this.date = date
                this.imageUri = imageUri
                this.isSynced = false
            }

            var serverId: Int? = null

            try {
                val createdItem = api.createCollage(newItem)
                serverId = createdItem.serverId
                newItem.isSynced = true
            } catch (e: Exception) {
                Log.e("Repo", "Offline: Saving locally as dirty.")
            }

            realm.write {
                val managedItem = copyToRealm(newItem)
                if (serverId != null) managedItem.serverId = serverId
            }
        }
    }

    suspend fun updateItem(id: ObjectId, updateBlock: (CollageItem) -> Unit) {
        withContext(Dispatchers.IO) {
            val (serverId, itemCopy) = realm.write {
                val item = query<CollageItem>("_id == $0", id).first().find()
                if (item != null) {
                    updateBlock(item)
                    item.isSynced = false
                    Pair(item.serverId, copyFromRealm(item))
                } else {
                    Pair(null, null)
                }
            }

            if (serverId != null && itemCopy != null) {
                try {
                    api.updateCollage(serverId, itemCopy)
                    realm.write {
                        query<CollageItem>("_id == $0", id).first().find()?.isSynced = true
                    }
                } catch (e: Exception) {
                    Log.e("Repo", "Offline: Update saved locally only.")
                }
            }
        }
    }

    suspend fun deleteItem(id: ObjectId) {
        withContext(Dispatchers.IO) {
            val item = realm.query<CollageItem>("_id == $0", id).first().find() ?: return@withContext

            if (item.serverId != null) {
                try {
                    api.deleteCollage(item.serverId!!)
                } catch (e: Exception) {
                    realm.write {
                        findLatest(item)?.apply {
                            isDeleted = true
                            isSynced = false
                        }
                    }
                    return@withContext
                }
            }

            realm.write {
                findLatest(item)?.let { delete(it) }
            }
        }
    }

    suspend fun syncLocalChanges() {
        withContext(Dispatchers.IO) {
            val unsyncedItems = realm.query<CollageItem>("isSynced == false AND isDeleted == false").find()

            unsyncedItems.forEach { item ->
                try {
                    val itemToSend = realm.copyFromRealm(item)
                    if (item.serverId == null) {
                        val created = api.createCollage(itemToSend)

                        realm.write {
                            findLatest(item)?.apply {
                                serverId = created.serverId
                                isSynced = true
                            }
                        }
                    } else {
                        api.updateCollage(item.serverId!!, itemToSend)

                        realm.write {
                            findLatest(item)?.isSynced = true
                        }
                    }
                    Log.d("Repo", "Synced item: ${item.title}")
                } catch (e: Exception) {
                    Log.e("Repo", "Sync failed for ${item.title}", e)
                }
            }

            val deletedItems = realm.query<CollageItem>(
                "isDeleted == true AND isSynced == false"
            ).find()

            deletedItems.forEach { item ->
                try {
                    if (item.serverId != null) {
                        api.deleteCollage(item.serverId!!)
                    }

                    realm.write {
                        findLatest(item)?.let { delete(it) }
                    }
                } catch (e: Exception) {
                    Log.e("Repo", "Sync delete failed", e)
                }
            }

        }
    }
    fun startWebSocket() {
        val request = RetrofitClient.getWebSocketRequest()
        val listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                handleSocketMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e("Repo", "WebSocket Error: ${t.message}")
            }
        }
        RetrofitClient.getClient().newWebSocket(request, listener)
    }

    private fun handleSocketMessage(json: String) {

        try {
            val message = Gson().fromJson(json, SocketMessage::class.java)

            // We parse the payload manually to avoid the Gson null-ID issue
            val payload = message.payload

            realm.writeBlocking {
                when (message.event) {
                    "created" -> {
                        // Check if we already have it
                        val serverId = payload.get("id").asInt
                        if (query<CollageItem>("serverId == $0", serverId).first().find() == null) {
                            val item = Gson().fromJson(payload, CollageItem::class.java)

                            // --- FIX: GENERATE ID ---
                            item._id = ObjectId()
                            // ------------------------

                            copyToRealm(item.apply { isSynced = true })
                        }
                    }
                    "updated" -> {
                        val serverId = payload.get("id").asInt
                        val local = query<CollageItem>("serverId == $0", serverId).first().find()
                        val item = Gson().fromJson(payload, CollageItem::class.java)

                        local?.apply {
                            title = item.title
                            description = item.description
                            rating = item.rating
                            date = item.date
                            imageUri = item.imageUri
                            isSynced = true
                        }
                    }
                    "deleted" -> {
                        val id = payload.get("id").asInt
                        val local = query<CollageItem>("serverId == $0", id).first().find()
                        if (local != null) delete(local)
                    }
                }
            }
            Log.d("Repo", "WebSocket handled event: ${message.event}")
        } catch (e: Exception) {
            Log.e("Repo", "WebSocket parse error", e)
        }
    }
}
data class SocketMessage(val event: String, val payload: com.google.gson.JsonObject)
