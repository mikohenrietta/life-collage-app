package com.example.lifecollage.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

        adapter = CollageAdapter()
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        viewModel.items.observe(this) { list ->
            adapter.submitList(list.toList())
        }
        viewModel.statusMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                android.util.Log.d("CollageApp", message)
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
                }
            }
        }
    }
}
