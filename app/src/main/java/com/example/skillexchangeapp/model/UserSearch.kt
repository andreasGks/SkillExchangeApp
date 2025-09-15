package com.example.skillexchangeapp.model

data class UserSearch(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profileImageUri: String = ""
) {
    val fullName: String
        get() = "$firstName $lastName"

    val uid: String
        get() = userId
}
