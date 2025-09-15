package com.example.skillexchangeapp.model

data class Chat(
    val id: String = "",
    val participants: List<String> = listOf(),
    val lastMessage: String = "",
    val lastTimestamp: Long = 0L
)
