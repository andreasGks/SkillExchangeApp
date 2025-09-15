package com.example.skillexchangeapp.afterlogin.feedscreen

import com.example.skillexchangeapp.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostRepository {
    private val db = FirebaseFirestore.getInstance()

    // Add Post (suspending)
    suspend fun addPost(post: Post) {
        val postMap = toMap(post)
        db.collection("posts")
            .add(postMap)
            .await()
    }

    // Get all posts (suspending)
    suspend fun getPosts(): List<Post> {
        val snapshot = db.collection("posts").get().await()
        return snapshot.documents.mapNotNull { doc ->
            fromMap(doc.data ?: emptyMap(), doc.id)
        }
    }
    
    
    // Get posts for a specific userId (suspending)
    suspend fun getPostsByUserId(userId: String): List<Post> {
        val snapshot = db.collection("posts")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            fromMap(doc.data ?: emptyMap(), doc.id)
        }
    }

    // Map Post to Map<String, Any?>
    private fun toMap(post: Post): Map<String, Any?> {
        return mapOf(
            "userId" to post.userId,
            "userName" to post.userName,
            "userImageUri" to post.userImageUri,
            "postImageUri" to post.postImageUri,
            "caption" to post.caption,
            "timestamp" to post.timestamp
        )
    }

    // Map Map<String, Any?> to Post
    private fun fromMap(map: Map<String, Any?>, id: String): Post {
        return Post(
            id = id,
            userId = map["userId"] as? String ?: "",
            userName = map["userName"] as? String ?: "",
            userImageUri = map["userImageUri"] as? String ?: "",
            postImageUri = map["postImageUri"] as? String,
            caption = map["caption"] as? String ?: "",
            timestamp = map["timestamp"] as? Long ?: 0L
        )
    }
}
