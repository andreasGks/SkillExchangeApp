//package com.example.skillexchangeapp
//
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.widget.ImageView
//import androidx.appcompat.app.AppCompatActivity
//import com.bumptech.glide.Glide
//import java.io.FileNotFoundException
//
//class FullScreenImageActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_full_screen_image)
//
//        val imageView: ImageView = findViewById(R.id.fullScreenImage)
//        val imageUrl = intent.getStringExtra("image_url")
//        Log.d("FullScreenImageActivity", "Received image URL: $imageUrl")
//
//        if (!imageUrl.isNullOrEmpty()) {
//            try {
//                val imageUri = Uri.parse(imageUrl)
//                if (imageUrl.startsWith("content://com.google.android.apps.photos.contentprovider")) {
//                    // Load image using ContentResolver (Google Photos workaround)
//                    loadImageFromUri(imageUri, imageView)
//                } else {
//                    // Load normally with Glide
//                    Glide.with(this)
//                        .load(imageUri)
//                        .into(imageView)
//                }
//            } catch (e: Exception) {
//                Log.e("FullScreenImageActivity", "Error parsing URI: $imageUrl", e)
//                imageView.setImageResource(R.drawable.icons_user) // Show default image
//            }
//        } else {
//            Log.e("FullScreenImageActivity", "Received null image URL")
//            imageView.setImageResource(R.drawable.icons_user) // Default image
//        }
//
//        // Close when user taps on the image
//        imageView.setOnClickListener {
//            finish()
//        }
//    }
//
//    private fun loadImageFromUri(imageUri: Uri, imageView: ImageView) {
//        try {
//            val inputStream = contentResolver.openInputStream(imageUri)
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            imageView.setImageBitmap(bitmap)
//        } catch (e: FileNotFoundException) {
//            Log.e("FullScreenImageActivity", "Image not found: $imageUri", e)
//            imageView.setImageResource(R.drawable.icons_user) // Default image
//        } catch (e: Exception) {
//            Log.e("FullScreenImageActivity", "Error loading image from URI", e)
//            imageView.setImageResource(R.drawable.icons_user) // Default image
//        }
//    }
//}




package com.example.skillexchangeapp.afterlogin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.skillexchangeapp.R
import java.io.FileNotFoundException
import java.io.InputStream

class FullScreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val imageView: ImageView = findViewById(R.id.fullScreenImage)
        val imageUrl = intent.getStringExtra("image_url")

        Log.d("FullScreenImageActivity", "Received image URL: $imageUrl")

        if (!imageUrl.isNullOrEmpty()) {
            val imageUri = Uri.parse(imageUrl)
            loadProfileImage(imageUri, imageView)
        } else {
            showDefaultImage(imageView)
        }

        // Close when user taps on the image
        imageView.setOnClickListener {
            finish()
        }
    }

    /**
     * Loads the profile image if available, otherwise shows the default icon.
     */
    private fun loadProfileImage(uri: Uri, imageView: ImageView) {
        try {
            when {
                uri.toString().startsWith("content://com.google.android.apps.photos.contentprovider") -> {
                    Log.d("FullScreenImageActivity", "Loading image from Google Photos")
                    loadBitmapFromUri(uri)?.let {
                        imageView.setImageBitmap(it)
                    } ?: showDefaultImage(imageView)
                }

                uri.toString().startsWith("content://") || uri.toString().startsWith("file://") -> {
                    Log.d("FullScreenImageActivity", "Loading image from local storage")
                    Glide.with(this)
                        .load(uri)

                        .into(imageView)
                }

                else -> {
                    Log.e("FullScreenImageActivity", "Unknown image source")
                    showDefaultImage(imageView)
                }
            }
        } catch (e: Exception) {
            Log.e("FullScreenImageActivity", "Error loading profile image", e)
            showDefaultImage(imageView)
        }
    }

    /**
     * Tries to load a bitmap from a URI.
     */
    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            Log.e("FullScreenImageActivity", "File not found: $uri", e)
            null
        } catch (e: Exception) {
            Log.e("FullScreenImageActivity", "Failed to load bitmap from URI", e)
            null
        }
    }

    /**
     * Displays the default profile icon.
     */
    private fun showDefaultImage(imageView: ImageView) {
        Log.d("FullScreenImageActivity", "Displaying default user icon")
        imageView.setImageResource(R.drawable.icons_user)
    }
}
