//package com.example.skillexchangeapp.ui
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Observer
//import com.example.skillexchangeapp.afterlogin.AfterLoginActivity
//import com.example.skillexchangeapp.createprofile.CreateAccountFragment
//import com.example.skillexchangeapp.beforelogin.login.ForgotPasswordFragment
//import com.example.skillexchangeapp.R
//import com.example.skillexchangeapp.login.LoginViewModel
//
//class LoginFragment : Fragment() {
//
//    private val loginViewModel: LoginViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_login, container, false)
//
//        val etEmail: EditText = view.findViewById(R.id.editTextEmail)
//        val etPassword: EditText = view.findViewById(R.id.editTextPassword)
//        val btnSubmitLogin: Button = view.findViewById(R.id.btnSubmitLogin)
//        val signUpTextView: TextView = view.findViewById(R.id.tvSignUp)
//        val forgotPasswordTextView: TextView = view.findViewById(R.id.tvForgotPassword)
//
//        // Redirect to AfterLoginActivity if user is already logged in
//        loginViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
//            if (user != null) {
//                startActivity(Intent(requireContext(), AfterLoginActivity::class.java))
//                requireActivity().finish()
//            }
//        })
//
//        btnSubmitLogin.setOnClickListener {
//            val email = etEmail.text.toString()
//            val password = etPassword.text.toString()
//
//            if (email.isNotEmpty() && password.isNotEmpty()) {
//                loginViewModel.login(email, password)
//            } else {
//                Toast.makeText(requireContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        return view
//    }
//}



package com.example.skillexchangeapp.beforelogin.login

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
import com.example.skillexchangeapp.afterlogin.AfterLoginActivity
import com.example.skillexchangeapp.beforelogin.login.createprofile.CreateAccountFragment
import com.example.skillexchangeapp.R

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

        // Observe current user and navigate if logged in
        loginViewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                navigateToAfterLogin()
            }
        })

        // Observe login result
        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                navigateToAfterLogin()
            }
        })

        // Observe error messages
        loginViewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                showToast(it)
            }
        })

        btnSubmitLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            } else {
                showToast("Please enter both email and password")
            }
        }

        signUpTextView.setOnClickListener {
            replaceFragment(CreateAccountFragment())
        }

        forgotPasswordTextView.setOnClickListener {
            replaceFragment(ForgotPasswordFragment())
        }

        return view
    }

    private fun navigateToAfterLogin() {
        startActivity(Intent(requireContext(), AfterLoginActivity::class.java))
        requireActivity().finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
