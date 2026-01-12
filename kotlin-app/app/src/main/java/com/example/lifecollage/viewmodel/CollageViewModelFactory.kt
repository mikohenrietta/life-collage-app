package com.example.lifecollage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifecollage.network.NetworkMonitor
import com.example.lifecollage.repository.CollageRepository
import io.realm.kotlin.Realm

class CollageViewModelFactory(
    private val realm: Realm,
    private val networkMonitor: NetworkMonitor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CollageViewModel(CollageRepository(realm), networkMonitor) as T
    }
}
