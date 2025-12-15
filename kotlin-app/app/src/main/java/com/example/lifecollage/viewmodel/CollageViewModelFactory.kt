package com.example.lifecollage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifecollage.repository.CollageRepository
import io.realm.kotlin.Realm

class CollageViewModelFactory(
    private val realm: Realm
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CollageViewModel(CollageRepository(realm)) as T
    }
}
