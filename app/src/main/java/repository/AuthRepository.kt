package repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Existing login function
    fun login(email: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }

    // New register function
    fun register(email: String, password: String, name: String, callback: (Boolean, String?) -> Unit) {
        // Check if the email already exists in Firestore
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Email already exists
                    callback(false, "This email is already in use.")
                } else {
                    // Proceed with account creation
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                user?.let {
                                    saveUserToDatabase(it, name) { success ->
                                        if (success) {
                                            callback(true, null)
                                        } else {
                                            callback(false, "Failed to save user data.")
                                        }
                                    }
                                } ?: callback(false, "User creation failed.")
                            } else {
                                task.exception?.message?.let { errorMessage ->
                                    Log.e("AuthRepository", "Error registering user: $errorMessage")
                                    callback(false, errorMessage)
                                } ?: callback(false, "Registration failed.")
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AuthRepository", "Error checking email existence: ${exception.message}")
                callback(false, "Error checking email.")
            }
    }



    // Save the user data to Firestore
    private fun saveUserToDatabase(user: FirebaseUser, name: String, callback: (Boolean) -> Unit) {
        val userMap = hashMapOf(
            "name" to name,
            "email" to user.email,
            "uid" to user.uid
        )

        firestore.collection("users")  // 'users' collection
            .document(user.uid)  // The document ID is the user UID
            .set(userMap)  // Save the user data
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}
