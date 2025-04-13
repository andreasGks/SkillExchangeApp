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

        // OLD: Load default fragment manually only if no saved state (safety)
        if (savedInstanceState == null) {
            Log.d("AfterLoginActivity", "Loading default fragment: FeedFragment.")
            loadFragment(FeedFragment()) // fallback/manual load
        }

        // OLD: Handle bottom nav manually (optional fallback or legacy)
        bottomNavView.setOnItemSelectedListener { item ->
            Log.d("AfterLoginActivity", "Bottom nav item selected: ${item.itemId}")
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.home -> FeedFragment()
                R.id.search -> SearchFragment()
                R.id.profile -> ProfileFragment()
                R.id.settings -> SettingsFragment()
                else -> FeedFragment()
            }
            loadFragment(selectedFragment)
            true
        }
    }

    // OLD: Manual fragment loading
    private fun loadFragment(fragment: Fragment) {
        Log.d("AfterLoginActivity", "Replacing fragment with: ${fragment.javaClass.simpleName}")
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment) // Updated ID to match NavHost
            .commit()
    }
}
