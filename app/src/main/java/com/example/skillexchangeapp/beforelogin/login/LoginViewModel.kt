package com.example.skillexchangeapp.beforelogin.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> get() = _currentUser

    init {
        // Check if user is already logged in
        _currentUser.value = authRepository.getCurrentUser()
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email and password cannot be empty."
            return
        }

        viewModelScope.launch {
            try {
                val success = authRepository.login(email, password)
                _loginResult.postValue(success)
                if (!success) {
                    _errorMessage.postValue("Login failed. Invalid credentials.")
                }
            } catch (e: Exception) {
                _loginResult.postValue(false)
                _errorMessage.postValue(e.message ?: "Something went wrong.")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _currentUser.value = null
    }
}
