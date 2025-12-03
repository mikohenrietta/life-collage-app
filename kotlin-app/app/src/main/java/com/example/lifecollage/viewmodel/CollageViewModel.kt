package com.example.lifecollage.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import com.example.lifecollage.model.CollageItem

class CollageViewModel: ViewModel() {
    private val initialItems = mutableListOf(
        CollageItem(1, "Beach trip", "Sunny day at the beach", "10", "2025-06-10", null),
        CollageItem(2, "Graduation", "Finally done!", "8", "2024-07-01", null)
    )
    private val _items = MutableLiveData<MutableList<CollageItem>>(initialItems)
    val items: LiveData<MutableList<CollageItem>> = _items
    private var nextId = initialItems.size + 1

    fun addItem(title: String, description: String, rating: String, date: String, imageUri: String?) {
        val newItem = CollageItem(nextId++, title, description, rating, date, imageUri)
        val current = _items.value ?: mutableListOf()
        current.add(newItem)
        _items.value = current
    }

    fun updateItem(updatedItem: CollageItem) {
        val current = _items.value ?: return
        val index = current.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            current[index] = updatedItem
            _items.value = current
        }
    }
        fun deleteItem(id: Int) {
            val current = _items.value ?: return
            val removed = current.removeAll { it.id == id }
            if(removed) {
                _items.value = current
            }
        }
    }