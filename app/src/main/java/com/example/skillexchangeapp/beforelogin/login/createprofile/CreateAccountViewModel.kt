package com.example.skillexchangeapp.beforelogin.login.createprofile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class CreateAccountViewModel : ViewModel() {

    fun register(email: String, password: String, fullName: String, callback: (Boolean, String?) -> Unit) {
        // Perform the registration logic, such as using Firebase
        // For example, using Firebase Authentication to create a user:

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration success
                    callback(true, null)
                } else {
                    // Registration failed
                    callback(false, task.exception?.message)
                }
            }
    }
}
