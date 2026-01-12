package com.example.lifecollage.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow

class NetworkMonitor(context: Context) {
    private val cm =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isOnline = MutableStateFlow(false)

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            isOnline.value = true
        }

        override fun onLost(network: Network) {
            isOnline.value = false
        }
    }

    init {
        val request = NetworkRequest.Builder().build()
        cm.registerNetworkCallback(request, callback)
    }
}