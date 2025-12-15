package com.example.lifecollage

import android.app.Application
import com.example.lifecollage.model.CollageItem
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class App : Application() {

    // 1. Create a variable to hold your database connection
    lateinit var realm: Realm

    override fun onCreate() {
        super.onCreate()

        // 2. Create the configuration
        // You MUST list all your RealmObject classes in the 'schema' set.
        // For now, it is empty because you haven't created a model yet.
        val config = RealmConfiguration.Builder(schema = setOf(CollageItem::class))
            .build()

        // 3. Open the Realm
        realm = Realm.open(config)
    }
}