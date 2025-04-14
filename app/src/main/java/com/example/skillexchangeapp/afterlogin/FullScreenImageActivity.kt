package com.example.skillexchangeapp.afterlogin

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.skillexchangeapp.R

class FullScreenImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val imageView: ImageView = findViewById(R.id.fullScreenImage)
        val imageUrl = intent.getStringExtra("image_url")

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(imageUrl))
                .error(R.drawable.icons_user) // fallback image
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.icons_user)
        }

        // Tap to close the screen
        imageView.setOnClickListener {
            finish()
        }
    }
}