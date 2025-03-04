package com.example.skillexchangeapp

import androidx.lifecycle.ViewModel
import repository.AuthRepository

class CreateAccountViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // Register user method
    fun register(email: String, password: String, name: String, callback: (Boolean, String?) -> Unit) {
        authRepository.register(email, password, name, callback)
    }

}
