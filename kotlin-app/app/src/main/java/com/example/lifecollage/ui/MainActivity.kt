package com.example.lifecollage.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifecollage.App
import com.example.lifecollage.R
import com.example.lifecollage.adapter.CollageAdapter
import com.example.lifecollage.model.CollageItem
import com.example.lifecollage.viewmodel.CollageViewModel
import com.example.lifecollage.viewmodel.CollageViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.mongodb.kbson.ObjectId

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CollageAdapter
    private lateinit var addButton: FloatingActionButton
    private lateinit var viewModel: CollageViewModel

    companion object {
        private const val ADD_ITEM_REQUEST_CODE = 1
        private const val DETAIL_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val realm = (application as App).realm
        val factory = CollageViewModelFactory(realm)
        viewModel = ViewModelProvider(this, factory)[CollageViewModel::class.java]

        recyclerView = findViewById(R.id.recyclerView)
        addButton = findViewById(R.id.addButton)

        adapter = CollageAdapter(mutableListOf())
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        // Observe LiveData
        viewModel.items.observe(this) { list ->
            // Only add new items if the adapter is empty at start
            if (adapter.itemCount == 0) {
                adapter.setItems(list)
            } else {
                // Find changes and update positions manually
                list.forEachIndexed { index, item ->
                    if (index < adapter.itemCount && adapter.currentItems[index].id == item.id) {
                        adapter.updateItemAt(index, item)
                    }
                }
            }
        }

        adapter.setOnItemClickListener { item ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("id", item.id.toHexString())
                putExtra("title", item.title)
                putExtra("description", item.description)
                putExtra("date", item.date)
                putExtra("rating", item.rating)
                putExtra("imageUri", item.imageUri)
            }
            startActivityForResult(intent, DETAIL_REQUEST_CODE)
        }

        addButton.setOnClickListener {
            val intent = Intent(this, AddCollageItemActivity::class.java)
            startActivityForResult(intent, ADD_ITEM_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) return

        when (requestCode) {
            ADD_ITEM_REQUEST_CODE -> {
                val title = data.getStringExtra("new_item_title") ?: ""
                val description = data.getStringExtra("new_item_description") ?: ""
                val date = data.getStringExtra("new_item_date") ?: ""
                val rating = data.getStringExtra("new_item_rating") ?: ""
                val imageUri = data.getStringExtra("new_item_imageUri")

                viewModel.addItem(title, description, rating, date, imageUri)
                
            }

            DETAIL_REQUEST_CODE -> {
                val deleteIdString = data.getStringExtra("delete_item_id")
                if (deleteIdString != null) {
                    val deleteId = ObjectId(deleteIdString)
                    viewModel.deleteItem(deleteId)

                    // Remove from adapter directly
                    val index = adapter.currentItems.indexOfFirst { it.id == deleteId }
                    if (index != -1) adapter.removeItemAt(index)
                    return
                }

                val updateIdString = data.getStringExtra("updated_item_id")
                if (updateIdString != null) {
                    val updateId = ObjectId(updateIdString)

                    viewModel.updateItem(updateId) { item ->
                        item.title = data.getStringExtra("updated_item_title") ?: ""
                        item.description = data.getStringExtra("updated_item_description") ?: ""
                        item.date = data.getStringExtra("updated_item_date") ?: ""
                        item.rating = data.getStringExtra("updated_item_rating") ?: ""
                        item.imageUri = data.getStringExtra("updated_item_imageUri")
                    }

                    // Update adapter directly
                    val index = adapter.currentItems.indexOfFirst { it.id == updateId }
                    if (index != -1) {
                        val updatedItem = viewModel.items.value?.get(index)
                        if (updatedItem != null) adapter.updateItemAt(index, updatedItem)
                    }
                }
            }
        }
    }
}
