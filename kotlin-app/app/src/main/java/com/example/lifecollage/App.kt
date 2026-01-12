package com.example.lifecollage

import android.app.Application
import com.example.lifecollage.model.CollageItem
import com.example.lifecollage.network.NetworkMonitor
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class App : Application() {

    lateinit var realm: Realm
    lateinit var networkMonitor: NetworkMonitor private set

    override fun onCreate() {
        super.onCreate()
        networkMonitor = NetworkMonitor(this)

        val config = RealmConfiguration.Builder(schema = setOf(CollageItem::class))
            .build()

        realm = Realm.open(config)

    }
}