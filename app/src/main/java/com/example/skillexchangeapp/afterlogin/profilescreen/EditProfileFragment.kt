package com.example.skillexchangeapp.afterlogin.profilescreen

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.skillexchangeapp.R

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private val profileViewModel: EditProfileViewModel by activityViewModels()

    private lateinit var editTextName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var textViewChangeProfilePhoto: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        editTextName = view.findViewById(R.id.firstName)
        editTextLastName = view.findViewById(R.id.lastName)
        editTextDescription = view.findViewById(R.id.description)
        btnSaveChanges = view.findViewById(R.id.saveButton)
        textViewChangeProfilePhoto = view.findViewById(R.id.editProfilePhotoText)

        // Observe the profile data
        profileViewModel.userName.observe(viewLifecycleOwner) { name ->
            editTextName.setText(name)
        }

        profileViewModel.lastName.observe(viewLifecycleOwner) { lastName ->
            editTextLastName.setText(lastName)
        }

        profileViewModel.description.observe(viewLifecycleOwner) { description ->
            editTextDescription.setText(description)
        }

        // Load the user profile data
        profileViewModel.loadUserProfile()

        // Save Changes Button
        btnSaveChanges.setOnClickListener {
            val name = editTextName.text.toString()
            val lastName = editTextLastName.text.toString()
            val description = editTextDescription.text.toString()

            // Update the profile data via ViewModel
            profileViewModel.saveUserProfile(name, lastName, description)

            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }
    }
}
