package com.example.skillexchangeapp.afterlogin

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Utility class for image transformations: Base64 encode/decode and downscale/upscale bitmaps.
 * All heavy operations run on Dispatchers.IO via coroutines.
 */
class ImageTransformer(private val contentResolver: ContentResolver) {


    companion object {
        /**
         * Returns a scaled bitmap suitable for full screen display.
         */
        fun transformForFullScreen(bitmap: Bitmap): Bitmap {
            // Εδώ μπορείς να το αλλάξεις αναλόγως — για παράδειγμα full HD
            val targetWidth = 1080
            val targetHeight = 1920
            return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        }
    }


    /**
     * Decode Base64 string to Bitmap on IO dispatcher.
     */
    suspend fun decodeBase64ToBitmap(base64: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Encode Bitmap to Base64 string on IO dispatcher.
     */
    suspend fun encodeBitmapToBase64(bitmap: Bitmap, quality: Int = 80): String? {
        return withContext(Dispatchers.IO) {
            try {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Load a Bitmap from a Uri on IO dispatcher.
     */
    suspend fun uriToBitmap(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                contentResolver.openInputStream(uri)?.use { input ->
                    BitmapFactory.decodeStream(input)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Downscale a bitmap to fit within maxWidth x maxHeight, preserving aspect ratio.
     */
    fun downscale(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        if (ratio >= 1f) return bitmap
        val newW = (width * ratio).toInt()
        val newH = (height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, newW, newH, true)
    }

    /**
     * Upscale a bitmap to targetWidth x targetHeight. Note: no quality gain beyond original.
     */
    fun upscale(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    /**
     * Full pipeline: Uri -> downscale -> encode Base64 on IO dispatcher.
     */
    suspend fun uriToBase64Downscaled(uri: Uri, maxWidth: Int, maxHeight: Int, quality: Int = 80): String? {
        val bmp = uriToBitmap(uri) ?: return null
        val down = downscale(bmp, maxWidth, maxHeight)
        return encodeBitmapToBase64(down, quality)
    }

    /**
     * Full pipeline: Base64 -> decode -> upscale -> Bitmap on IO dispatcher.
     */
    suspend fun base64ToBitmapUpscaled(base64: String, targetWidth: Int, targetHeight: Int): Bitmap? {
        val decoded = decodeBase64ToBitmap(base64) ?: return null
        return upscale(decoded, targetWidth, targetHeight)
    }
}
