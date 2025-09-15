//package com.example.skillexchangeapp.afterlogin.profilescreen
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.example.skillexchangeapp.model.UserSearch
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//
//object SelectedCategoriesRepository {
//
//    private val selectedCategories = MutableLiveData<List<String>>(emptyList())
//    private val db = FirebaseFirestore.getInstance()
//    private val auth = FirebaseAuth.getInstance()
//
//    fun getSelectedCategories(): LiveData<List<String>> = selectedCategories
//
//    fun addCategory(category: String) {
//        val userId = auth.currentUser?.uid ?: return
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val categoryData = hashMapOf(
//                    "category" to category,
//                    "timestamp" to System.currentTimeMillis()
//                )
//                db.collection("users")
//                    .document(userId)
//                    .collection("selectedCategories")
//                    .add(categoryData)
//                    .await()
//
//                // Ενημερώνουμε το LiveData τοπικά μετά το save
//                val currentList = selectedCategories.value?.toMutableList() ?: mutableListOf()
//                if (!currentList.contains(category)) {
//                    currentList.add(category)
//                    selectedCategories.postValue(currentList)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    fun removeCategory(category: String) {
//        val userId = auth.currentUser?.uid ?: return
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                // Ψάχνουμε τα docs που έχουν αυτή την κατηγορία για να τα διαγράψουμε
//                val querySnapshot = db.collection("users")
//                    .document(userId)
//                    .collection("selectedCategories")
//                    .whereEqualTo("category", category)
//                    .get()
//                    .await()
//
//                for (doc in querySnapshot.documents) {
//                    db.collection("users")
//                        .document(userId)
//                        .collection("selectedCategories")
//                        .document(doc.id)
//                        .delete()
//                        .await()
//                }
//
//                // Ενημερώνουμε το LiveData τοπικά μετά το delete
//                val currentList = selectedCategories.value?.toMutableList() ?: mutableListOf()
//                if (currentList.contains(category)) {
//                    currentList.remove(category)
//                    selectedCategories.postValue(currentList)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    fun clearCategories() {
//        selectedCategories.value = emptyList()
//    }
//
//    fun loadSelectedCategoriesFromFirestore() {
//        val userId = auth.currentUser?.uid ?: return
//        db.collection("users")
//            .document(userId)
//            .collection("selectedCategories")
//            .get()
//            .addOnSuccessListener { result ->
//                val categories = result.documents.mapNotNull { it.getString("category") }
//                selectedCategories.value = categories
//            }
//            .addOnFailureListener {
//                it.printStackTrace()
//            }
//    }
//
//    suspend fun searchUsersWithFilter(query: String, filters: List<String>): List<UserSearch> {
//        return try {
//            val allUsers = db.collection("users")
//                .get()
//                .await()
//
//            allUsers.documents.mapNotNull { doc ->
//                val user = doc.toObject(UserSearch::class.java)
//                val uid = auth.currentUser?.uid
//
//                if (user != null && user.uid != uid) {
//                    val categoriesSnapshot = db.collection("users")
//                        .document(user.uid!!)
//                        .collection("selectedCategories")
//                        .get()
//                        .await()
//
//                    val userCategories = categoriesSnapshot.documents.mapNotNull {
//                        it.getString("category")
//                    }
//
//                    // Αν έχει έστω ένα φίλτρο που υπάρχει στις κατηγορίες του χρήστη
//                    if (filters.any { userCategories.contains(it) }) {
//                        if (query.isBlank() || user.fullName.contains(query, ignoreCase = true)) {
//                            user
//                        } else null
//                    } else null
//                } else null
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            emptyList()
//        }
//    }
//
//
//}
package com.example.skillexchangeapp.afterlogin.profilescreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.skillexchangeapp.model.UserSearch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SelectedCategoriesRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val selectedCategories = MutableLiveData<List<String>>(emptyList())

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    // --- LiveData ---
    suspend fun getSelectedCategoriesForProfile(userId: String, onSuccess: (List<String>) -> Unit) {
        val result = loadSelectedCategoriesWithResult(userId)
        result.getOrNull()?.let {
            onSuccess(it)
        }
    }

    fun getSelectedCategories(userId: String? = null): LiveData<List<String>> {
        val uid = userId ?: getCurrentUserId() ?: return selectedCategories
        loadSelectedCategoriesFromFirestore(uid)
        return selectedCategories
    }

    // --- Add category ---
    fun addCategory(category: String, userId: String? = null) {
        val uid = userId ?: getCurrentUserId() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users")
                    .document(uid)
                    .update("selectedCategories", FieldValue.arrayUnion(category))
                    .await()
                val currentList = selectedCategories.value?.toMutableList() ?: mutableListOf()
                if (!currentList.contains(category)) {
                    currentList.add(category)
                    selectedCategories.postValue(currentList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- Remove category ---
    fun removeCategory(category: String, userId: String? = null) {
        val uid = userId ?: getCurrentUserId() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users")
                    .document(uid)
                    .update("selectedCategories", FieldValue.arrayRemove(category))
                    .await()
                val currentList = selectedCategories.value?.toMutableList() ?: mutableListOf()
                if (currentList.contains(category)) {
                    currentList.remove(category)
                    selectedCategories.postValue(currentList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- Clear categories ---
    fun clearCategories(userId: String? = null) {
        val uid = userId ?: getCurrentUserId() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users")
                    .document(uid)
                    .update("selectedCategories", emptyList<String>())
                    .await()
                selectedCategories.postValue(emptyList())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- Load categories from Firestore ---
    fun loadSelectedCategoriesFromFirestore(userId: String? = null) {
        val uid = userId ?: getCurrentUserId() ?: return
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val categories = doc.get("selectedCategories") as? List<String> ?: emptyList()
                selectedCategories.value = categories
            }
            .addOnFailureListener { it.printStackTrace() }
    }

    suspend fun loadSelectedCategoriesWithResult(userId: String): Result<List<String>> {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            val categories = doc.get("selectedCategories") as? List<String> ?: emptyList()
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Set entire categories list ---
    fun setCategories(categories: List<String>, userId: String? = null) {
        val uid = userId ?: getCurrentUserId() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users")
                    .document(uid)
                    .update("selectedCategories", categories)
                    .await()
                selectedCategories.postValue(categories)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- Search users with filter ---
    suspend fun searchUsersWithFilter(
        query: String,
        filters: List<String>,
        currentUserId: String? = null
    ): List<UserSearch> {
        val uid = currentUserId ?: getCurrentUserId() ?: return emptyList()
        return try {
            val allUsers = db.collection("users").get().await()
            allUsers.documents.mapNotNull { doc ->
                val user = doc.toObject(UserSearch::class.java)
                if (user != null && user.uid != uid) {
                    val userCategories =
                        doc.get("selectedCategories") as? List<String> ?: emptyList()
                    if (filters.isEmpty() || filters.any { userCategories.contains(it) }) {
                        if (query.isBlank() || user.fullName.contains(
                                query,
                                ignoreCase = true
                            )
                        ) user else null
                    } else null
                } else null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

