package com.example.skillexchangeapp.afterlogin.profilescreen

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.skillexchangeapp.afterlogin.profilescreen.EditProfileViewModel

class EditProfileViewModel : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> get() = _userName

    private val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String> get() = _lastName

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description

    // Fetch user profile data from Firebase
    fun loadUserProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userProfile = document.toObject(UserProfile::class.java)
                        _userName.value = userProfile?.name
                        _lastName.value = userProfile?.lastName
                        _description.value = userProfile?.description
                    }
                }
                .addOnFailureListener {
                    // Handle failure to fetch data
                    Log.e("EditProfileViewModel", "Error loading profile", it)
                }
        }
    }

    // Save user profile data to Firebase
    fun saveUserProfile(name: String, lastName: String, description: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userProfile = UserProfile(name, lastName, description)
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .set(userProfile)
                .addOnSuccessListener {
                    // Handle success
                }
                .addOnFailureListener {
                    // Handle failure to save data
                    Log.e("EditProfileViewModel", "Error saving profile", it)
                }
        }
    }
}
