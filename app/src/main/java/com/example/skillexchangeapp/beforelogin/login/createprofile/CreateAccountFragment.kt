//package com.example.skillexchangeapp
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import com.example.skillexchangeapp.login.LoginFragment
//
//class CreateAccountFragment : Fragment() {
//
//    private lateinit var firstNameInput: EditText
//    private lateinit var lastNameInput: EditText
//    private lateinit var nickNameInput: EditText
//    private lateinit var emailInput: EditText
//    private lateinit var passwordInput: EditText
//    private lateinit var confirmPasswordInput: EditText
//    private lateinit var btnRegister: Button
//    private lateinit var goToLoginTextView: TextView
//
//    private val createAccountViewModel: CreateAccountViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_create_account, container, false)
//
//        // Find views by ID
//        firstNameInput = view.findViewById(R.id.editTextFirstName)
//        lastNameInput = view.findViewById(R.id.editTextLastName)
//        nickNameInput = view.findViewById(R.id.editTextNickName)
//        emailInput = view.findViewById(R.id.editTextEmailInput)
//        passwordInput = view.findViewById(R.id.editTextPasswordInput)
//        confirmPasswordInput = view.findViewById(R.id.editTextConfirmPasswordInput)
//        btnRegister = view.findViewById(R.id.btnRegister)
//        goToLoginTextView = view.findViewById(R.id.tvGoToLogin)
//
//        // Set onClick listener for register button
//        btnRegister.setOnClickListener {
//            val firstName = firstNameInput.text.toString()
//            val lastName = lastNameInput.text.toString()
//            val nickName = nickNameInput.text.toString()
//            val email = emailInput.text.toString()
//            val password = passwordInput.text.toString()
//            val confirmPassword = confirmPasswordInput.text.toString()
//
//            // Validate the inputs with more detailed error messages
//            when {
//                email.isEmpty() -> {
//                    Toast.makeText(requireContext(), "Email field cannot be empty", Toast.LENGTH_SHORT).show()
//                }
//                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
//                    Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
//                }
//                password.isEmpty() -> {
//                    Toast.makeText(requireContext(), "Password field cannot be empty", Toast.LENGTH_SHORT).show()
//                }
//                password.length < 6 -> {
//                    Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
//                }
//                confirmPassword.isEmpty() -> {
//                    Toast.makeText(requireContext(), "Please confirm your password", Toast.LENGTH_SHORT).show()
//                }
//                password != confirmPassword -> {
//                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
//                }
//                firstName.isEmpty() -> {
//                    Toast.makeText(requireContext(), "First Name cannot be empty", Toast.LENGTH_SHORT).show()
//                }
//                lastName.isEmpty() -> {
//                    Toast.makeText(requireContext(), "Last Name cannot be empty", Toast.LENGTH_SHORT).show()
//                }
//                else -> {
//                    // If all validations pass, proceed with registration
//                    createAccountViewModel.register(
//                        email,
//                        password,
//                        firstName + " " + lastName,  // Combining first and last name
//                        ::onRegistrationResult
//                    )
//                }
//            }
//        }
//
//        // Set onClick listener for login text
//        goToLoginTextView.setOnClickListener {
//            val loginFragment = LoginFragment()
//            requireActivity().supportFragmentManager.beginTransaction()
//                .setCustomAnimations(
//                    R.anim.slide_in_right,
//                    R.anim.slide_out_left,
//                    R.anim.slide_in_left,
//                    R.anim.slide_out_right
//                )
//                .replace(R.id.fragment_container, loginFragment)
//                .addToBackStack(null)
//                .commit()
//        }
//
//        return view
//    }
//
//    // Callback method for registration result
//    private fun onRegistrationResult(success: Boolean, errorMessage: String?) {
//        if (success) {
//            Toast.makeText(requireContext(), "Account Created Successfully", Toast.LENGTH_SHORT).show()
//
//            // Delay for 2 seconds before navigating to LoginFragment
//            view?.postDelayed({
//                val loginFragment = LoginFragment()
//                requireActivity().supportFragmentManager.beginTransaction()
//                    .setCustomAnimations(
//                        R.anim.slide_in_right,
//                        R.anim.slide_out_left,
//                        R.anim.slide_in_left,
//                        R.anim.slide_out_right
//                    )
//                    .replace(R.id.fragment_container, loginFragment)
//                    .addToBackStack(null)
//                    .commit()
//            }, 2000) // 2000ms = 2 seconds delay
//        } else {
//            Toast.makeText(requireContext(), errorMessage ?: "Failed to create account. Please try again.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//}


package com.example.skillexchangeapp.beforelogin.login.createprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.beforelogin.login.LoginFragment

class CreateAccountFragment : Fragment() {

    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var nickNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var btnRegister: Button
    private lateinit var goToLoginTextView: TextView

    private val createAccountViewModel: CreateAccountViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_account, container, false)

        // Find views by ID
        firstNameInput = view.findViewById(R.id.editTextFirstName)
        lastNameInput = view.findViewById(R.id.editTextLastName)
        nickNameInput = view.findViewById(R.id.editTextNickName)
        emailInput = view.findViewById(R.id.editTextEmailInput)
        passwordInput = view.findViewById(R.id.editTextPasswordInput)
        confirmPasswordInput = view.findViewById(R.id.editTextConfirmPasswordInput)
        btnRegister = view.findViewById(R.id.btnRegister)
        goToLoginTextView = view.findViewById(R.id.tvGoToLogin)

        // Set onClick listener for register button
        // Set onClick listener for register button
        btnRegister.setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val nickName = nickNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            // Validate the inputs with more detailed error messages
            when {
                email.isEmpty() -> {
                    Toast.makeText(requireContext(), "Email field cannot be empty", Toast.LENGTH_SHORT).show()
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(requireContext(), "Invalid email format", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(requireContext(), "Password field cannot be empty", Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                }
                confirmPassword.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please confirm your password", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
                firstName.isEmpty() -> {
                    Toast.makeText(requireContext(), "First Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                lastName.isEmpty() -> {
                    Toast.makeText(requireContext(), "Last Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // If all validations pass, proceed with registration
                    val fullName = "$firstName $lastName" // Correctly combining first name + last name
                    createAccountViewModel.register(
                        email,
                        password,
                        fullName,  // Now correctly passing full name as the username
                        ::onRegistrationResult
                    )
                }
            }
        }


        // Set onClick listener for login text
        goToLoginTextView.setOnClickListener {
            val loginFragment = LoginFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, loginFragment)
        }

        return view
    }

    private fun onRegistrationResult(success: Boolean, errorMessage: String?) {
        if (success) {
            Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
            // Navigate to the login screen after successful registration
            val loginFragment = LoginFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, loginFragment)                .commit()
        } else {
            Toast.makeText(requireContext(), errorMessage ?: "Registration failed", Toast.LENGTH_SHORT).show()
        }
    }
}
