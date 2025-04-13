//package com.example.skillexchangeapp
//
//import android.os.Bundle
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.fragment.app.Fragment
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.FirebaseApp
//import com.google.firebase.auth.FirebaseUser
//
//class MainActivity : AppCompatActivity() {
//
//    var toolbar: Toolbar? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Initialize Firebase
//        FirebaseApp.initializeApp(this)
//
//        // Set up logo
//        val logoImageView: ImageView = findViewById(R.id.logoImageView)
//
//        // Set up the toolbar
//        toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        // Hide the default ActionBar
//        supportActionBar?.hide()
//
//        // Create a default user if necessary
//        createDefaultUser()
//
//        // Load the default fragment
//        if (savedInstanceState == null) {
//            loadFragment(MainFragment())
//        }
//    }
//
//    private fun loadFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .commit()
//    }
//
//    // Function to create a default user (if no user is signed in)
//    private fun createDefaultUser() {
//        val auth = FirebaseAuth.getInstance()
//
//        val email = "defaultuser@example.com"  // Default email
//        val password = "password123"  // Default password
//
//        // Check if the user is already signed in
//        val currentUser = auth.currentUser
//        if (currentUser == null) {
//            // Create the user if they don't exist
//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val user = auth.currentUser
//                        Toast.makeText(this, "User created with UID: ${user?.uid}", Toast.LENGTH_SHORT).show()
//
//                        // Create the user in Firestore with default details
//                        createFirestoreUser(user)
//                    } else {
//                        // Handle error
//                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//    }
//
//    // Function to create the Firestore user document
//    private fun createFirestoreUser(user: FirebaseUser?) {
//        val db = FirebaseFirestore.getInstance()
//        val userId = user?.uid ?: return  // Ensure the user ID exists
//
//        // Default user details
//        val userMap = hashMapOf(
//            "userName" to "Default User",  // Default user name
//            "userImageUri" to "android.resource://com.example.skillexchangeapp/2131165367" // Default user image URI
//        )
//
//        // Add user to Firestore under "users" collection
//        db.collection("users").document(userId)
//            .set(userMap)
//            .addOnSuccessListener {
//                Toast.makeText(this, "User details added to Firestore", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//}
package com.example.skillexchangeapp.beforelogin.login

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.skillexchangeapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔥 Removed `FirebaseApp.initializeApp(this)` because it's now in MyApplication.kt ✅

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

        // Check if a user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return  // ✅ User already logged in, no need to create a default user
        }

        val email = "defaultuser@example.com"
        val password = "password123"

        // Check if this email already exists before trying to create a new user
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods != null && signInMethods.isNotEmpty()) {
                        // ✅ Email is already in use, so do not create a new user
                        Log.d("MainActivity", "Default user already exists. Skipping creation.")
                    } else {
                        //  Email does not exist, so create the user
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { createTask ->
                                if (createTask.isSuccessful) {
                                    val user = auth.currentUser
                                    Toast.makeText(this, "User created with UID: ${user?.uid}", Toast.LENGTH_SHORT).show()
                                    createFirestoreUser(user)
                                } else {
                                    Toast.makeText(this, "Error: ${createTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "Error checking email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    // Function to create the Firestore user document
    private fun createFirestoreUser(user: FirebaseUser?) {
        val db = FirebaseFirestore.getInstance()
        val userId = user?.uid ?: return  // Ensure the user ID exists

        // Default user details
        val userMap = hashMapOf(
            "userName" to "Default User",  // Default user name
            "userImageUri" to "android.resource://com.example.skillexchangeapp/2131165367" // Default user image URI
        )

        // Add user to Firestore under "users" collection
        db.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "User details added to Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
