package com.example.lifecollage.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lifecollage.R
import com.example.lifecollage.adapter.CollageAdapter
import com.example.lifecollage.model.CollageItem
import com.example.lifecollage.viewmodel.CollageViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
        viewModel = ViewModelProvider(this).get(CollageViewModel::class.java)

        recyclerView = findViewById(R.id.recyclerView)
        addButton = findViewById(R.id.addButton)


        adapter = CollageAdapter(
            viewModel.items.value ?: mutableListOf(),
        )

        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter

        viewModel.items.observe(this) { updatedList ->
            adapter.updateList(updatedList)
        }

        adapter.setOnItemClickListener { item ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("id", item.id)
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

        if (resultCode == Activity.RESULT_OK && data != null) {
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
                    val deleteId = data.getIntExtra("delete_id", -1)
                    if (deleteId != -1) {
                        viewModel.deleteItem(deleteId)
                    }

                    val updatedId = data.getIntExtra("updated_item_id", -1)
                    if (updatedId != -1) {
                        val updatedItem = CollageItem(
                            id = updatedId,
                            title = data.getStringExtra("updated_item_title") ?: "",
                            description = data.getStringExtra("updated_item_description") ?: "",
                            date = data.getStringExtra("updated_item_date") ?: "",
                            rating = data.getStringExtra("updated_item_rating") ?: "",
                            imageUri = data.getStringExtra("updated_item_imageUri")
                        )

                        viewModel.updateItem(updatedItem)
                    }
            }
            }
        }
    }
}
