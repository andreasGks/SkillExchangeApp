package com.example.skillexchangeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.skillexchangeapp.ui.LoginFragment

class ForgotPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        // Get references to UI elements
        val fullNameEditText = view.findViewById<EditText>(R.id.editTextNewFirstName)
        val newPasswordEditText = view.findViewById<EditText>(R.id.editTextNewPassword)
        val confirmPasswordEditText = view.findViewById<EditText>(R.id.editTextNewConfirmPasswordInput)
        val resetButton = view.findViewById<Button>(R.id.btnResetPassword)

        // Handle button click
        resetButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (fullName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your full name", Toast.LENGTH_SHORT).show()
            } else if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter and confirm your password", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Implement backend password reset logic
                Toast.makeText(requireContext(), "Password reset successfully!", Toast.LENGTH_SHORT).show()

                // Redirect back to the LoginFragment
                val loginFragment = LoginFragment()
                (requireActivity() as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, loginFragment)
                    .commit()
            }
        }

        return view
    }
}
