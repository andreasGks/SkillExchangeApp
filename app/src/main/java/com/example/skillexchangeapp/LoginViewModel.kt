package com.example.skillexchangeapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import repository.AuthRepository

class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()  // ✅ Correct reference

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    fun login(email: String, password: String) {
        authRepository.login(email, password) { success ->
            _loginResult.postValue(success)
        }
    }
}
