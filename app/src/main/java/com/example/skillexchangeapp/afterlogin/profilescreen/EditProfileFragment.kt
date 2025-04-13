package com.example.skillexchangeapp.afterlogin.profilescreen

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.skillexchangeapp.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var editTextName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextNickname: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextCurrentPassword: EditText
    private lateinit var btnValidatePassword: Button
    private lateinit var btnSaveChanges: Button
    private lateinit var textViewChangeProfilePhoto: TextView

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("EditProfileFragment", "EditProfileFragment has been reached!")

        // Initialize views
        editTextName = view.findViewById(R.id.firstName)
        editTextLastName = view.findViewById(R.id.lastName)
        editTextEmail = view.findViewById(R.id.editTextEmail) // Ensure the email is not editable
        editTextPassword = view.findViewById(R.id.editTextPassword)

        btnSaveChanges = view.findViewById(R.id.saveButton) // Button from the layout
        textViewChangeProfilePhoto = view.findViewById(R.id.editProfilePhotoText)

        // Load existing user data
        profileViewModel.userName.observe(viewLifecycleOwner) { editTextName.setText(it) }
        profileViewModel.profileImageUri.observe(viewLifecycleOwner) { /* Handle image if needed */ }

        // Fetch extra user details (Last Name, Email, Nickname, Password)
        auth.currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        editTextLastName.setText(document.getString("lastName") ?: "")
                        editTextEmail.setText(document.getString("email") ?: "")
                        editTextNickname.setText(document.getString("nickname") ?: "")
                        editTextPassword.setText(document.getString("password") ?: "")
                    }
                }
        }

        // Validate Password
        btnValidatePassword.setOnClickListener {
            val currentPassword = editTextCurrentPassword.text.toString()
            if (currentPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Enter your current password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Re-authenticate user
            val credential = EmailAuthProvider.getCredential(auth.currentUser!!.email!!, currentPassword)
            auth.currentUser!!.reauthenticate(credential)
                .addOnSuccessListener {
                    enableFields(true)
                    btnSaveChanges.isEnabled = true // Enable the save button after successful re-authentication
                    Toast.makeText(requireContext(), "Password verified! You can edit your details.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show()
                }
        }

        // Save Changes
        btnSaveChanges.setOnClickListener {
            val updates = hashMapOf(
                "userName" to editTextName.text.toString(),
                "lastName" to editTextLastName.text.toString(),
                "nickname" to editTextNickname.text.toString()
            )

            db.collection("users").document(auth.currentUser!!.uid)
                .update(updates as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun enableFields(enable: Boolean) {
        editTextName.isEnabled = enable
        editTextLastName.isEnabled = enable
        editTextEmail.isEnabled = false // Email should not be editable
        editTextNickname.isEnabled = enable
        editTextPassword.isEnabled = false // Password should not be changed here
    }
}
