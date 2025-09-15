package com.example.skillexchangeapp.model

data class UserProfile(
    val firstname: String = "",
    val lastName: String = "",
    val description: String = "",
    val profilePhoto: String = "",
    val location: String = "",
    val selectedCategories: List<String> = emptyList()
)
