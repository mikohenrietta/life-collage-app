package com.example.lifecollage.model

data class CollageItem (
    val id: Int,
    var title: String,
    var description: String,
    var rating: String,
    var date: String,
    val imageUri: String? = null
)