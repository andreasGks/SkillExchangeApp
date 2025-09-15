package com.example.skillexchangeapp.afterlogin

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Object για να κάνουμε κοινό utility method για profile images σε διάφορα fragments
// Επιλέξαμε object γιατί δεν χρειάζεται να κρατάει δικό του state ή να δημιουργείται instance.
object ProfileImageUtils {

    /**
     * Παρατηρεί (observe) ένα LiveData<String> που περιέχει εικόνα σε Base64
     * και μόλις αλλάξει:
     * - Κάνει decode το Base64 string σε Bitmap (στο background thread)
     * - Upscale το Bitmap σε επιθυμητές διαστάσεις (background)
     * - Κάνει set το τελικό Bitmap στο ImageView (στο main thread)
     *
     * @param lifecycleOwner Το fragment/activity που "ζει" αυτό το LiveData
     * @param liveData Το LiveData που περιέχει την εικόνα σε Base64 μορφή
     * @param lifecycleScope Το CoroutineScope του fragment/activity
     * @param imageTransformer Κλάση που χειρίζεται decoding και scaling εικόνας
     * @param imageView Το ImageView που θέλουμε να εμφανίσουμε την εικόνα
     * @param defaultIconResId Το drawable που θα μπει αν η εικόνα είναι null/invalid
     */
    fun observeProfileImage(
        lifecycleOwner: LifecycleOwner,
        liveData: LiveData<String>,
        lifecycleScope: LifecycleCoroutineScope,
        imageTransformer: ImageTransformer,
        imageView: ImageView,
        defaultIconResId: Int
    ) {
        // Παρακολουθεί το LiveData για αλλαγές στο Base64 string
        liveData.observe(lifecycleOwner) { base64String ->
            if (!base64String.isNullOrEmpty()) {
                try {
                    // Background Thread για decoding και upscaling
                    lifecycleScope.launch(Dispatchers.IO) {
                        // Decode Base64 string σε Bitmap
                        val profileImageBitmap = imageTransformer.decodeBase64ToBitmap(base64String)
                            ?: return@launch // αν αποτύχει, σταμάτα

                        // Upscale το Bitmap στις διαστάσεις 400x400 (ή ό,τι ζητήσεις)
                        val originalProfileBitmap = imageTransformer.upscale(profileImageBitmap, 400, 400)

                        // Επιστροφή στο Main Thread για να γίνει το set στο ImageView
                        withContext(Dispatchers.Main) {
                            imageView.setImageBitmap(
                                originalProfileBitmap
                                    ?: BitmapFactory.decodeResource(imageView.resources, defaultIconResId)
                            )
                        }
                    }
                } catch (e: Exception) {
                    // Αν κάτι πάει στραβά, κάνε log και βάλε default εικόνα
                    Log.e("ProfileImageUtils", "Error decoding Base64 image", e)
                    imageView.setImageResource(defaultIconResId)
                }
            } else {
                // Αν δεν υπάρχει εικόνα στο Base64 string, βάλε default εικόνα
                imageView.setImageResource(defaultIconResId)
            }
        }
    }
}
