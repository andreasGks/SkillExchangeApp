package com.example.skillexchangeapp.afterlogin.searchscreen

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SearchViewModel : ViewModel() {

    // Function to log out the user
    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}
