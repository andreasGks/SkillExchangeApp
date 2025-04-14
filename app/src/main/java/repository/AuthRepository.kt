package repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Check if the user is already logged in
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Login function with improved error handling
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Check if user data exists in Firestore, if not, create it
                        checkUserInDatabase(it.uid, email, callback)
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Login failed due to unknown error"
                    Log.e("AuthRepository", "Login error: $errorMessage")
                    callback(false, errorMessage)
                }
            }
    }

    // Register function (fixed firstName + lastName handling)
    fun register(
        email: String,
        password: String,
        firstName: String,  // Corrected name
        lastName: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods != null && signInMethods.isNotEmpty()) {
                        callback(false, "This email is already in use.")
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { registerTask ->
                                if (registerTask.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.let {
                                        saveUserToDatabase(it, firstName, lastName, callback)
                                    } ?: callback(false, "User creation failed.")
                                } else {
                                    val errorMessage = registerTask.exception?.message ?: "Registration failed."
                                    callback(false, errorMessage)
                                }
                            }
                    }
                } else {
                    callback(false, "Error checking email existence: ${task.exception?.message}")
                }
            }
    }

    // Save user to Firestore
    private fun saveUserToDatabase(
        user: FirebaseUser,
        firstName: String,
        lastName: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val nickName = "$firstName $lastName"

        val userMap = hashMapOf(
            "uid" to user.uid,
            "email" to user.email,
            "userName" to firstName,
            "lastName" to lastName,
            "nickName" to nickName
        )

        Log.d("AuthRepository", "Saving user to Firestore with UID: ${user.uid}")

        firestore.collection("users")
            .document(user.uid)
            .set(userMap)
            .addOnSuccessListener {
                Log.d("AuthRepository", "User ${user.email} saved to database successfully")
                callback(true, null)
            }
            .addOnFailureListener { exception ->
                val errorMessage = exception.message ?: "Unknown error saving user data"
                Log.e("AuthRepository", "Error saving user to Firestore: $errorMessage")
                callback(false, errorMessage)
            }
    }

    // Check if the user exists in Firestore
    private fun checkUserInDatabase(
        userId: String,
        email: String,
        callback: (Boolean, String?) -> Unit
    ) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    val userMap = hashMapOf(
                        "uid" to userId,
                        "email" to email,
                        "firstName" to "",
                        "lastName" to "",
                        "nickName" to ""
                    )
                    firestore.collection("users")
                        .document(userId)
                        .set(userMap)
                        .addOnSuccessListener {
                            Log.d("AuthRepository", "User $email added to Firestore")
                            callback(true, null)
                        }
                        .addOnFailureListener { exception ->
                            val errorMessage = exception.message ?: "Error adding user to Firestore"
                            Log.e("AuthRepository", "Error adding user to Firestore: $errorMessage")
                            callback(false, errorMessage)
                        }
                } else {
                    callback(true, null)
                }
            }
            .addOnFailureListener { exception ->
                val errorMessage = exception.message ?: "Error checking user data in Firestore"
                Log.e("AuthRepository", "Error checking user in Firestore: $errorMessage")
                callback(false, errorMessage)
            }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }
}
