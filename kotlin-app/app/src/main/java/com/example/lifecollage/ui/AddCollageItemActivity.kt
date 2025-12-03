package com.example.lifecollage.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.example.lifecollage.R
import com.example.lifecollage.model.CollageItem
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts

class AddCollageItemActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var ratingEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: Button
    private lateinit var selectedImageView: ImageView
    private lateinit var selectedImageButton: Button
    private var currentImageUri: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)

            currentImageUri = it.toString()
            selectedImageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_collage_item)

        titleEditText = findViewById(R.id.titleText)
        descriptionEditText = findViewById(R.id.descriptionText)
        dateEditText = findViewById(R.id.dateText)
        ratingEditText = findViewById(R.id.ratingText)
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)
        selectedImageView = findViewById(R.id.selectedImage)
        selectedImageButton = findViewById(R.id.selectedImageButton)

        backButton.setOnClickListener {
            finish()
        }
        selectedImageButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val date = dateEditText.text.toString().trim()
            val rating = ratingEditText.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || date.isEmpty() || rating.isEmpty() || !rating.isDigitsOnly() || rating.toInt() !in 0..10) {
                titleEditText.error = if (title.isEmpty()) "Required" else null
                descriptionEditText.error = if (description.isEmpty()) "Required" else null
                dateEditText.error = if (date.isEmpty()) "Required" else null
                if(rating.isEmpty()){
                    ratingEditText.error = "Required"
                }
                else if(!rating.isDigitsOnly()){
                    ratingEditText.error = "Rating has to be a number"
                }
                else if(rating.toInt() !in 0..10){
                    ratingEditText.error = "Rating has to be between 0 and 10"
                }
                else ratingEditText.error = null

                return@setOnClickListener
            }

            val resultIntent = Intent()
            resultIntent.putExtra("new_item_title", title)
            resultIntent.putExtra("new_item_description", description)
            resultIntent.putExtra("new_item_date", date)
            resultIntent.putExtra("new_item_rating", rating)
            resultIntent.putExtra("new_item_imageUri", currentImageUri)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
