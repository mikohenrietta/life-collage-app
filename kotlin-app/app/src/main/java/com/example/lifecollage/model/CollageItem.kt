package com.example.lifecollage.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class CollageItem : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId()
    var title: String = ""
    var description: String = ""
    var rating: String = ""
    var date: String = ""
    var imageUri: String? = null
}