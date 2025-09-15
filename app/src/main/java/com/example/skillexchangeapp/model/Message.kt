package com.example.skillexchangeapp.model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    val sentByMe: Boolean = false  // 👈 για το UI του ChatAdapter
)
