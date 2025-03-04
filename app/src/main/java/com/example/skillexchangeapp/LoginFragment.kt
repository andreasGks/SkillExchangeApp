package com.example.skillexchangeapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.skillexchangeapp.AfterLoginActivity
import com.example.skillexchangeapp.CreateAccountFragment
import com.example.skillexchangeapp.ForgotPasswordFragment
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val etEmail: EditText = view.findViewById(R.id.editTextEmail)
        val etPassword: EditText = view.findViewById(R.id.editTextPassword)
        val btnSubmitLogin: Button = view.findViewById(R.id.btnSubmitLogin)
        val signUpTextView: TextView = view.findViewById(R.id.tvSignUp)
        val forgotPasswordTextView: TextView = view.findViewById(R.id.tvForgotPassword)

        // Observe login result
        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                val intent = Intent(requireContext(), AfterLoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        })

        // Handle Login Button Click
        btnSubmitLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(requireContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle "Sign Up" click
        signUpTextView.setOnClickListener {
            val createAccountFragment = CreateAccountFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, createAccountFragment)
                .addToBackStack(null)
                .commit()
        }

        // Handle "Forgot Password?" click
        forgotPasswordTextView.setOnClickListener {
            val forgotPasswordFragment = ForgotPasswordFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, forgotPasswordFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
