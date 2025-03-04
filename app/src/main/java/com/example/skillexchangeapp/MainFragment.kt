package com.example.skillexchangeapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.skillexchangeapp.ui.LoginFragment

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        // Set the toolbar (if needed)
        //val toolbar: Toolbar = view.findViewById(R.id.toolbar)

        // Initialize UI elements
        val btnLogin: Button = view.findViewById(R.id.btnLogin)
        val btnRegister: Button = view.findViewById(R.id.btnRegister)
        val tvForgotPassword: TextView = view.findViewById(R.id.tvForgotPassword)

        // Navigate to LoginFragment when the Login button is clicked
        btnLogin.setOnClickListener {
            Log.d("MainFragment", "Login button clicked")
            loadFragment(LoginFragment())
        }

        // Navigate to CreateAccountFragment when the Register button is clicked
        btnRegister.setOnClickListener {
            Log.d("MainFragment", "Register button clicked")
            loadFragment(CreateAccountFragment())
        }

        // Navigate to ForgotPasswordFragment when the "Forgot Password?" text is clicked
        tvForgotPassword.setOnClickListener {
            Log.d("MainFragment", "Forgot Password clicked")
            loadFragment(ForgotPasswordFragment())
        }

        return view
    }

    // Helper function to load a new fragment
    private fun loadFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true) // Ensures smoother transitions without UI flicker
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Enables back navigation
            .commit()
    }

}
