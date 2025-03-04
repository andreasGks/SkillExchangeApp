//package com.example.skillexchangeapp
//
//import android.os.Bundle
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.fragment.app.Fragment
//
//class MainActivity : AppCompatActivity() {
//
//    var toolbar: Toolbar? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val logoImageView: ImageView = findViewById(R.id.logoImageView)
//
//
//        toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        // Hide the default ActionBar
//        supportActionBar?.hide()
//
//
//        // Load the default fragment
//        if (savedInstanceState == null) {
//            loadFragment(MainFragment())
//        }
//
//    }
//    private fun loadFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .commit()
//    }
//}



package com.example.skillexchangeapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Set up logo
        val logoImageView: ImageView = findViewById(R.id.logoImageView)

        // Set up the toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Hide the default ActionBar
        supportActionBar?.hide()

        // Create a default user if necessary
        createDefaultUser()

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(MainFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Function to create a default user (if no user is signed in)
    private fun createDefaultUser() {
        val auth = FirebaseAuth.getInstance()

        val email = "defaultuser@example.com"  // Default email
        val password = "password123"  // Default password

        // Check if the user is already signed in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Create the user if they don't exist
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User created successfully
                        val user = auth.currentUser
                        Toast.makeText(this, "User created with UID: ${user?.uid}", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle error
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
