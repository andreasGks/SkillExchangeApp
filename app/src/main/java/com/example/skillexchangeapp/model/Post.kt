package com.example.skillexchangeapp.model

import com.google.firebase.firestore.DocumentId

data class Post(
    @DocumentId val id: String = "",
    val userId: String = "",
    var userName: String = "",
    var userImageUri: String = "",
    val postImageUri: String? = null,
    val caption: String = "",
    val timestamp: Long = System.currentTimeMillis()
)