package repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Check if the user is already logged in
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Login function with coroutine support and improved error handling
    suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val user = auth.currentUser
            if (user != null) {
                // Check if user data exists in Firestore, if not, create it
                checkUserInDatabase(user.uid, email)
                true
            } else {
                Log.e("AuthRepository", "Login failed: user is null after sign-in")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login error: ${e.message}", e)
            false
        }
    }

    // Register function with coroutine support
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Boolean {
        return try {
            val signInMethods = auth.fetchSignInMethodsForEmail(email).await().signInMethods
            if (signInMethods != null && signInMethods.isNotEmpty()) {
                Log.e("AuthRepository", "This email is already in use.")
                false
            } else {
                auth.createUserWithEmailAndPassword(email, password).await()
                val user = auth.currentUser
                if (user != null) {
                    saveUserToDatabase(user, firstName, lastName)
                    true
                } else {
                    Log.e("AuthRepository", "User creation failed: user is null after registration")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration error: ${e.message}", e)
            false
        }
    }

    // Save user to Firestore (suspend)
    private suspend fun saveUserToDatabase(
        user: FirebaseUser,
        firstName: String,
        lastName: String
    ) {
        val nickName = "$firstName $lastName"
        val defaultProfilePhoto = "https://example.com/default_profile_photo.png" // Replace with actual URL

        val userMap = hashMapOf(
            "uid"          to user.uid,
            "email"        to user.email,
            "firstName"    to firstName,
            "lastName"     to lastName,
            "nickName"     to nickName,
            "profilePhoto" to defaultProfilePhoto,
            "description"  to "",
            "location"     to ""
        )

        Log.d("AuthRepository", "Saving user to Firestore with UID: ${user.uid}")

        firestore.collection("users")
            .document(user.uid)
            .set(userMap)
            .await()

        Log.d("AuthRepository", "User ${user.email} saved to database successfully")
    }

    // Check if user exists in Firestore, if not create it (suspend)
    private suspend fun checkUserInDatabase(userId: String, email: String) {
        try {
            val document = firestore.collection("users").document(userId).get().await()
            if (!document.exists()) {
                val userMap = hashMapOf(
                    "uid"          to userId,
                    "email"        to email,
                    "firstName"    to "",
                    "lastName"     to "",
                    "nickName"     to "",
                    "profilePhoto" to "",
                    "location"     to ""
                )
                firestore.collection("users").document(userId).set(userMap).await()
                Log.d("AuthRepository", "User $email added to Firestore")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error checking/adding user in Firestore: ${e.message}", e)
            throw e // προωθούμε το error αν χρειαστεί να χειριστεί πιο πάνω
        }
    }

    // Update user location (suspend)
    suspend fun updateUserLocation(newLocation: String): Boolean {
        val currentUser = auth.currentUser ?: return false
        return try {
            firestore.collection("users")
                .document(currentUser.uid)
                .update("location", newLocation)
                .await()
            Log.d("AuthRepository", "Location updated to: $newLocation")
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error updating location: ${e.message}", e)
            false
        }
    }

    // Logout (sync)
    fun logout() {
        auth.signOut()
    }
}
