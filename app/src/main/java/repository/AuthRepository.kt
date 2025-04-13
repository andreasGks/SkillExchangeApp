//
//package repository
//
//import android.util.Log
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.firestore.FirebaseFirestore
//
//class AuthRepository {
//
//    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
//
//    // Check if the user is already logged in
//    fun getCurrentUser(): FirebaseUser? {
//        return auth.currentUser
//    }
//
//    // Login function with improved error handling
//    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d("AuthRepository", "Login successful for $email")
//                    callback(true, null)
//                } else {
//                    val errorMessage = task.exception?.message ?: "Login failed due to unknown error"
//                    Log.e("AuthRepository", "Login error: $errorMessage")
//                    callback(false, errorMessage)
//                }
//            }
//    }
//
//    // Register function (updated to include lastName and userName)
//    fun register(
//        email: String,
//        password: String,
//        name: String,
//        lastName: String,
//        userName: String,
//        callback: (Boolean, String?) -> Unit
//    ) {
//        auth.fetchSignInMethodsForEmail(email)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val signInMethods = task.result?.signInMethods
//                    if (signInMethods != null && signInMethods.isNotEmpty()) {
//                        // Email already exists
//                        callback(false, "This email is already in use.")
//                    } else {
//                        // Email does not exist, create new user
//                        auth.createUserWithEmailAndPassword(email, password)
//                            .addOnCompleteListener { registerTask ->
//                                if (registerTask.isSuccessful) {
//                                    val user = auth.currentUser
//                                    user?.let {
//                                        saveUserToDatabase(it, name, lastName, userName, callback)
//                                    } ?: callback(false, "User creation failed.")
//                                } else {
//                                    val errorMessage = registerTask.exception?.message ?: "Registration failed."
//                                    callback(false, errorMessage)
//                                }
//                            }
//                    }
//                } else {
//                    callback(false, "Error checking email existence: ${task.exception?.message}")
//                }
//            }
//    }
//
//    // Save user to Firestore with the new schema (added lastName and userName)
//    private fun saveUserToDatabase(
//        user: FirebaseUser,
//        name: String,
//        lastName: String,
//        userName: String,
//        callback: (Boolean, String?) -> Unit
//    ) {
//        val userMap = hashMapOf(
//            "uid" to user.uid,
//            "email" to user.email,
//            "name" to name,
//            "lastName" to lastName,  // Added lastName to Firestore
//            "userName" to userName   // Added userName to Firestore
//        )
//
//        firestore.collection("users")
//            .document(user.uid)
//            .set(userMap)
//            .addOnSuccessListener {
//                Log.d("AuthRepository", "User ${user.email} saved to database successfully")
//                callback(true, null)
//            }
//            .addOnFailureListener { exception ->
//                val errorMessage = exception.message ?: "Unknown error saving user data"
//                Log.e("AuthRepository", "Error saving user to Firestore: $errorMessage")
//                callback(false, errorMessage)
//            }
//    }
//
//
//    // Logout function
//    fun logout() {
//        auth.signOut()
//    }
//}
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

    // Register function (fixed username handling)
    fun register(
        email: String,
        password: String,
        fullName: String,  // Now correctly passing fullName as username
        lastName: String,
        nickName: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods != null && signInMethods.isNotEmpty()) {
                        // Email already exists
                        callback(false, "This email is already in use.")
                    } else {
                        // Email does not exist, create new user
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { registerTask ->
                                if (registerTask.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.let {
                                        // Save the new user to Firestore
                                        saveUserToDatabase(it, fullName, lastName, nickName, callback)
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

    // Save user to Firestore with the new schema (fixed username logic)
    private fun saveUserToDatabase(
        user: FirebaseUser,
        fullName: String,
        lastName: String,
        nickName: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val userMap = hashMapOf(
            "uid" to user.uid,
            "email" to user.email,
            "userName" to fullName,  // Now correctly storing fullName as username
            "lastName" to lastName,
            "nickName" to nickName  // Keeping the nickname if needed
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

    // Check if the user exists in the Firestore collection, if not, create the record
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
                    // If the user doesn't exist, create a new user document
                    val userMap = hashMapOf(
                        "uid" to userId,
                        "email" to email,
                        "userName" to "",  // Default empty userName
                        "lastName" to "",  // Default empty lastName
                        "nickName" to ""   // Default empty nickName
                    )
                    firestore.collection("users")
                        .document(userId)
                        .set(userMap)
                        .addOnSuccessListener {
                            Log.d("AuthRepository", "User ${email} added to Firestore")
                            callback(true, null)
                        }
                        .addOnFailureListener { exception ->
                            val errorMessage = exception.message ?: "Error adding user to Firestore"
                            Log.e("AuthRepository", "Error adding user to Firestore: $errorMessage")
                            callback(false, errorMessage)
                        }
                } else {
                    // User already exists in Firestore
                    callback(true, null)
                }
            }
            .addOnFailureListener { exception ->
                val errorMessage = exception.message ?: "Error checking user data in Firestore"
                Log.e("AuthRepository", "Error checking user in Firestore: $errorMessage")
                callback(false, errorMessage)
            }
    }

    // Logout function
    fun logout() {
        auth.signOut()
    }
}


