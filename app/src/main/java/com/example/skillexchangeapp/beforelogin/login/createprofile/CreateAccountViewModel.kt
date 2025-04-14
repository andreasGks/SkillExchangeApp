package com.example.skillexchangeapp.beforelogin.login.createprofile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
class CreateAccountViewModel : ViewModel() {

    fun register(email: String, password: String, fullName: String, callback: (Boolean, String?) -> Unit) {
        val auth = Firebase.auth
        val firestore = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val userMap = hashMapOf(
                        "uid" to uid,
                        "email" to email,
                        "fullName" to fullName,
                        "firstName" to fullName.split(" ").firstOrNull(),
                        "lastName" to fullName.split(" ").lastOrNull()
                    )

                    // Save user data to Firestore
                    firestore.collection("users").document(uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            callback(true, null)
                        }
                        .addOnFailureListener { e ->
                            callback(false, "Failed to save user data: ${e.message}")
                        }
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

}
