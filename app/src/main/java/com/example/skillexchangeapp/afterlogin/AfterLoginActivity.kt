package com.example.skillexchangeapp.afterlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.beforelogin.login.MainActivity
import com.example.skillexchangeapp.afterlogin.feedscreen.FeedFragment
import com.example.skillexchangeapp.afterlogin.profilescreen.ProfileFragment
import com.example.skillexchangeapp.afterlogin.searchscreen.SearchFragment
import com.example.skillexchangeapp.afterlogin.settingsscreen.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class AfterLoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_login)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavView.visibility = View.VISIBLE

        // Log user authentication status
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d("AfterLoginActivity", "User is not logged in. Redirecting to login screen.")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        } else {
            Log.d("AfterLoginActivity", "User logged in as: ${currentUser.email}")
        }

        // NEW: Setup NavController and connect it with BottomNavigationView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        Log.d("AfterLoginActivity", "NavController is set up with BottomNavigationView.")
        bottomNavView.setupWithNavController(navController)


    }

}
