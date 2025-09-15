package repository

import android.util.Log
import com.example.skillexchangeapp.model.UserSearch
import com.example.skillexchangeapp.model.Offer
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SearchRepository {

    private val db = FirebaseFirestore.getInstance()
    private val offersRepository = OffersRepository()

    private fun mapUserDocument(doc: Map<String, Any?>, docId: String): UserSearch {
        val firstName = doc["firstName"] as? String ?: ""
        val lastName = doc["lastName"] as? String ?: ""
        val profileImageUri = doc["profileImageUri"] as? String ?: ""
        return UserSearch(userId = docId, firstName = firstName, lastName = lastName, profileImageUri = profileImageUri)
    }

    suspend fun searchUsers(query: String): List<UserSearch> = try {
        val snapshot = db.collection("users").get().await()
        Log.d("SearchRepository", "searchUsers - total users in DB: ${snapshot.size()}")
        snapshot.documents.mapNotNull { doc ->
            val user = mapUserDocument(doc.data ?: emptyMap(), doc.id)
            if (user.firstName.contains(query, true) || user.lastName.contains(query, true)) user else null
        }.also { Log.d("SearchRepository", "searchUsers - matched users: ${it.size}") }
    } catch (e: Exception) {
        Log.e("SearchRepository", "Error in searchUsers", e)
        emptyList()
    }

    suspend fun getAllUsers(): List<UserSearch> = try {
        val snapshot = db.collection("users").get().await()
        Log.d("SearchRepository", "getAllUsers - total users: ${snapshot.size()}")
        snapshot.documents.map { doc -> mapUserDocument(doc.data ?: emptyMap(), doc.id) }
    } catch (e: Exception) {
        Log.e("SearchRepository", "Error in getAllUsers", e)
        emptyList()
    }

    suspend fun searchUsersWithFilter(query: String, filters: List<String>): List<UserSearch> = try {
        val snapshot = db.collection("users").get().await()
        Log.d("SearchRepository", "searchUsersWithFilter - total users in DB: ${snapshot.size()}")
        snapshot.documents.mapNotNull { doc ->
            val user = mapUserDocument(doc.data ?: emptyMap(), doc.id)
            val userSkills = doc.get("skills") as? List<String> ?: emptyList()
            val matchesQuery = query.isEmpty() || user.firstName.contains(query, true) || user.lastName.contains(query, true)
            val matchesFilter = filters.isEmpty() || filters.any { filter -> userSkills.contains(filter) }
            if (matchesQuery && matchesFilter) user else null
        }.also { Log.d("SearchRepository", "searchUsersWithFilter - matched users: ${it.size}") }
    } catch (e: Exception) {
        Log.e("SearchRepository", "Error in searchUsersWithFilter", e)
        emptyList()
    }

    suspend fun searchUsersByCategories(categories: List<String>): List<UserSearch> {
        return try {
            Log.d("SearchRepository", "searchUsersByCategories - input categories: $categories")

            val offers: List<Offer> = offersRepository.getOffersByCategories(categories)
            val userIds = offers.mapNotNull { it.userId }.distinct()
            Log.d("SearchRepository", "Unique userIds from offers: $userIds")

            if (userIds.isEmpty()) return emptyList()

            val users = mutableListOf<UserSearch>()
            val chunks = userIds.chunked(10) // Firestore allows max 10 in whereIn

            for (chunk in chunks) {
                val snapshot = db.collection("users")
                    .whereIn("userId", chunk)
                    .get()
                    .await()
                val chunkUsers = snapshot.documents.map { doc ->
                    mapUserDocument(doc.data ?: emptyMap(), doc.id)
                }
                users.addAll(chunkUsers)
            }

            Log.d("SearchRepository", "Total users returned: ${users.size}")
            users
        } catch (e: Exception) {
            Log.e("SearchRepository", "Error in searchUsersByCategories", e)
            emptyList()
        }
    }
}
