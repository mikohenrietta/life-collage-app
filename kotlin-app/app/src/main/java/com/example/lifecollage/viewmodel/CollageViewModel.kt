package com.example.lifecollage.viewmodel

import androidx.lifecycle.*
import com.example.lifecollage.repository.CollageRepository
import com.example.lifecollage.model.CollageItem
import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class CollageViewModel(private val repository: CollageRepository) : ViewModel() {

    private val _items = MutableLiveData<List<CollageItem>>()
    val items: LiveData<List<CollageItem>> = _items

    init {
        viewModelScope.launch {
            repository.getAllItems().asFlow().collect { changes: ResultsChange<CollageItem> ->
                _items.value = changes.list.toList()
            }
        }
    }

    fun addItem(title: String, description: String, rating: String, date: String, imageUri: String?) {
        viewModelScope.launch {
            repository.addItem(title, description, rating, date, imageUri)
        }
    }

    fun updateItem(id: ObjectId, update: (CollageItem) -> Unit) {
        viewModelScope.launch {
            repository.updateItem(id, update)
        }
    }

    fun deleteItem(id: ObjectId) {
        viewModelScope.launch {
            repository.deleteItem(id)
        }
    }
}
