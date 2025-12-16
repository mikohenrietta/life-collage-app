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

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    init {
        viewModelScope.launch {
            try {
                repository.getAllItems().asFlow().collect { changes: ResultsChange<CollageItem> ->
                    _items.value = changes.list.toList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _statusMessage.value = "Error retrieving data: ${e.message}"
            }
        }
    }

    fun addItem(title: String, description: String, rating: String, date: String, imageUri: String?) {
        viewModelScope.launch {
            try {
                repository.addItem(title, description, rating, date, imageUri)
                _statusMessage.value = "Item added successfully"
            } catch (e: Exception) {
                e.printStackTrace()
                _statusMessage.value = "Error adding item: ${e.message}"
            }
        }
    }

    fun updateItem(id: ObjectId, update: (CollageItem) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateItem(id, update)
            } catch (e: Exception) {
                e.printStackTrace()
                _statusMessage.value = "Error updating item: ${e.message}"
            }
        }
    }

    fun deleteItem(id: ObjectId) {
        viewModelScope.launch {
            try {
                repository.deleteItem(id)
            } catch (e: Exception) {
                e.printStackTrace()
                _statusMessage.value = "Error deleting item: ${e.message}"
            }
        }
    }
}
