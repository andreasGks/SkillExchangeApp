package repository

import com.example.skillexchangeapp.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserProfileRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val uid: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun loadUserProfile(): UserProfile? {
        val userId = uid ?: return null
        val doc = firestore.collection("users")
            .document(userId)
            .get()
            .await()
        if (!doc.exists()) return null

        return UserProfile(
            firstname   = doc.getString("firstName").orEmpty(),
            lastName    = doc.getString("lastName").orEmpty(),
            description = doc.getString("description").orEmpty(),
            profilePhoto = doc.getString("profilePhoto").orEmpty(),
            location    = doc.getString("location").orEmpty()
        )
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val userId = uid ?: return
        val updates = mapOf(
            "firstName"   to profile.firstname,
            "lastName"    to profile.lastName,
            "description" to profile.description,
            "location"    to profile.location
        )
        firestore.collection("users")
            .document(userId)
            .update(updates)
            .await()
    }

    suspend fun getUserProfileById(userId: String): UserProfile? {
        val doc = firestore.collection("users")
            .document(userId)
            .get()
            .await()

        return if (doc.exists()) {
            UserProfile(
                firstname = doc.getString("firstName").orEmpty(),
                lastName = doc.getString("lastName").orEmpty(),
                description = doc.getString("description").orEmpty(),
                profilePhoto = doc.getString("profilePhoto").orEmpty(),
                location = doc.getString("location").orEmpty()
            )
        } else null
    }

}
