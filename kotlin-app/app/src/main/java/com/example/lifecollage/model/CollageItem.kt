package com.example.lifecollage.model
import com.google.gson.annotations.SerializedName
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class CollageItem : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()

    @SerializedName("id")
    var serverId: Int? = null

    var title: String = ""
    var description: String = ""
    var rating: String = ""
    var date: String = ""
    var imageUri: String? = null

    var isSynced: Boolean = true
    var isDeleted: Boolean = false
}