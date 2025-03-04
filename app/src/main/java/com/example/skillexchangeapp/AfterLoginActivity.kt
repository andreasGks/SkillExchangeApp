package com.example.skillexchangeapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AfterLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_login)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavView.visibility = View.VISIBLE

        // Load the default fragment when the activity starts
        if (savedInstanceState == null) {
            loadFragment(FeedFragment()) // Open FeedFragment by default
        }

        // Handle bottom navigation selection
        bottomNavView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.home -> FeedFragment()
                R.id.search -> SearchFragment()
                R.id.profile -> ProfileFragment()
                R.id.settings -> SettingsFragment()
                else -> FeedFragment() // Default
            }
            loadFragment(selectedFragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
