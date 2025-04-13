package com.example.skillexchangeapp.afterlogin

import com.google.firebase.firestore.DocumentId

data class Post(
    @DocumentId val id: String = "", // Firestore document ID
    val userId: String = "", // User ID of the post creator
    var userName: String = "",
    var userImageUri: String = "", // Store URI as a String for Firestore
    val postImageUri: String? = null, // Nullable because a post might not have an image
    val caption: String = ""
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "userName" to userName,
            "userImageUri" to userImageUri,
            "postImageUri" to postImageUri,
            "caption" to caption
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Post {
            return Post(
                userId = map["userId"] as? String ?: "",
                userName = map["userName"] as? String ?: "",
                userImageUri = map["userImageUri"] as? String ?: "",
                postImageUri = map["postImageUri"] as? String,
                caption = map["caption"] as? String ?: ""
            )
        }
    }
}
