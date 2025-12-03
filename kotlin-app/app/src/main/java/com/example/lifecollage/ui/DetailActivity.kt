package com.example.lifecollage.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lifecollage.R
import com.example.lifecollage.model.CollageItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.net.URI
import androidx.core.net.toUri

class DetailActivity : AppCompatActivity() {

    private lateinit var titleText: TextView
    private lateinit var dateText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var ratingText: TextView
    private lateinit var backButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var itemImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collage_item_details)

        titleText = findViewById(R.id.itemTitle)
        dateText = findViewById(R.id.itemDate)
        descriptionText = findViewById(R.id.itemDescription)
        ratingText = findViewById(R.id.itemRating)
        backButton = findViewById(R.id.backButton)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)
        itemImage = findViewById(R.id.itemImage)

        val id = intent.getIntExtra("id", -1)
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val date = intent.getStringExtra("date")
        val rating = intent.getStringExtra("rating")
        val imageUriString = intent.getStringExtra("imageUri")

        if (imageUriString != null){
            itemImage.setImageURI(imageUriString.toUri())
        }

        titleText.text = title
        dateText.text = date
        descriptionText.text = description
        ratingText.text = "Rating: $rating"

        backButton.setOnClickListener {
            finish()
        }

        editButton.setOnClickListener {
            val intent = Intent(this, UpdateCollageItemActivity::class.java).apply {
                putExtra("id", id)
                putExtra("title", title)
                putExtra("description", description)
                putExtra("date", date)
                putExtra("rating",rating)
                putExtra("imageUri", imageUriString)
            }
            startActivityForResult(intent, 3)
        }

        deleteButton.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete item?")
                .setMessage("Are you sure you want to delete \"$title\"?")
                .setPositiveButton("Yes") { _, _ ->
                    val intent = Intent().apply {
                        putExtra("delete_id", id)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 3 && resultCode == Activity.RESULT_OK && data != null) {
            val updatedItemId = data.getIntExtra("updated_item_id", -1)
            val updatedTitle = data.getStringExtra("updated_item_title") ?: ""
            val updatedDescription = data.getStringExtra("updated_item_description") ?: ""
            val updatedDate = data.getStringExtra("updated_item_date") ?: ""
            val updatedRating = data.getStringExtra("updated_item_rating") ?: ""
            val updatedImageUri = data.getStringExtra("updated_item_imageUri")

            val resultIntent = Intent().apply {
                putExtra("updated_item_id", updatedItemId)
                putExtra("updated_item_title", updatedTitle)
                putExtra("updated_item_description", updatedDescription)
                putExtra("updated_item_date", updatedDate)
                putExtra("updated_item_rating", updatedRating)
                putExtra("updated_item_imageUri", updatedImageUri)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

}
