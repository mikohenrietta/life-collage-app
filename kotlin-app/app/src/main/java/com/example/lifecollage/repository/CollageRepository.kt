package com.example.lifecollage.repository

import com.example.lifecollage.model.CollageItem
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import org.mongodb.kbson.ObjectId

class CollageRepository(private val realm: Realm) {

    fun getAllItems() = realm.query<CollageItem>().find()

    suspend fun addItem(
        title: String,
        description: String,
        rating: String,
        date: String,
        imageUri: String?
    ) {
        realm.write {
            val item = copyToRealm(CollageItem().apply {
                this.title = title
                this.description = description
                this.rating = rating
                this.date = date
                this.imageUri = imageUri
            })
        }
    }

    suspend fun updateItem(id: ObjectId, block: (CollageItem) -> Unit) {
        realm.write {
            val item = query<CollageItem>("id == $0", id).first().find()
            item?.let { block(it) }
        }
    }

    suspend fun deleteItem(id: ObjectId) {
        realm.write {
            val item = query<CollageItem>("id == $0", id).first().find()
            item?.let { delete(it) }
        }
    }
}
