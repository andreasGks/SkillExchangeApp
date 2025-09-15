package com.example.skillexchangeapp.afterlogin

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.skillexchangeapp.R
import android.util.Base64
import android.widget.Toast


class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        imageView = findViewById(R.id.fullScreenImage)

        val imageBytes = intent.getByteArrayExtra("image_bytes")
        if (imageBytes != null) {
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            imageView.setImageBitmap(bitmap)
            return
        }

        val imageUrlOrBase64 = intent.getStringExtra("IMAGE_URL_KEY")

        if (imageUrlOrBase64.isNullOrEmpty()) {
            Toast.makeText(this, "No image to display", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (imageUrlOrBase64.startsWith("content://") || imageUrlOrBase64.startsWith("file://")) {
            Glide.with(this)
                .load(Uri.parse(imageUrlOrBase64))
                .into(imageView)
        } else {
            try {
                val decodedBytes = Base64.decode(imageUrlOrBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
