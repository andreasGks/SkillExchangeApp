package com.example.skillexchangeapp.afterlogin.profilescreen

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _fullName = MutableLiveData<String>()
    val fullName: LiveData<String> = _fullName

    private val _profileImageUri = MutableLiveData<String>()
    val profileImageUri: LiveData<String> = _profileImageUri

    init {
        fetchUserData()
    }

    fun fetchUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    _userName.value = document.getString("userName") ?: "Unknown User"
                    _fullName.value = document.getString("fullName") ?: "Unknown Full Name"
                    _profileImageUri.value = document.getString("profileImageUri") ?: ""
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().getReference("profile_images/${auth.currentUser?.uid}")
        storageRef.putFile(uri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                updateProfileImageInDatabase(downloadUrl.toString())
            }
        }
    }

    private fun updateProfileImageInDatabase(imageUrl: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .update("profileImageUri", imageUrl)
                .addOnSuccessListener {
                    fetchUserData() // Refresh user data after updating the image
                }
        }
    }
}
