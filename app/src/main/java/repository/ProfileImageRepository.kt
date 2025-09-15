package repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileImageRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun uploadProfileImage(image: String): Boolean {
        val uid = userId ?: return false
        return withContext(Dispatchers.IO) {
            try {
                firestore.collection("users").document(uid)
                    .update("profilePhoto", image)
                    .await()
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun getProfileImage(): String? {
        val uid = userId ?: return null
        return withContext(Dispatchers.IO) {
            try {
                val document = firestore.collection("users").document(uid).get().await()
                document.getString("profilePhoto")
            } catch (e: Exception) {
                null
            }
        }
    }
}
