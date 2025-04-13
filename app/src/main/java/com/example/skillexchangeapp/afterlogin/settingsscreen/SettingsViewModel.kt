package com.example.skillexchangeapp.afterlogin.settingsscreen

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun logout() {
        auth.signOut() // Logs out the user
    }
}
