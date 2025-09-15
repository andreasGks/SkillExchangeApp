package com.example.skillexchangeapp.beforelogin.login.createprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

class CreateAccountViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun register(email: String, password: String, fullName: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                // Δημιουργία χρήστη στο Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = authResult.user?.uid ?: throw Exception("User ID is null")

                val defaultProfilePhoto = "https://example.com/default_profile_photo.png" // Default photo URL
                val firstName = fullName.split(" ").firstOrNull() ?: ""
                val lastName = fullName.split(" ").lastOrNull() ?: ""

                val userMap = hashMapOf(
                    "uid" to uid,
                    "email" to email,
                    "fullName" to fullName,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "profilePhoto" to defaultProfilePhoto
                )

                // Αποθήκευση των στοιχείων χρήστη στο Firestore
                firestore.collection("users").document(uid).set(userMap).await()

                // Αν όλα πήγαν καλά
                callback(true, null)
            } catch (e: Exception) {
                // Κάτι πήγε στραβά
                callback(false, e.message)
            }
        }
    }
}
