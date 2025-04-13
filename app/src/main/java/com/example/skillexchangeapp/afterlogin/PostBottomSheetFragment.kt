package com.example.skillexchangeapp.afterlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.databinding.FragmentPostBottomSheetBinding
import com.example.skillexchangeapp.afterlogin.feedscreen.FeedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPostBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by activityViewModels()
    private var imageUri: Uri? = null  // Now, handling URI.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBottomSheetBinding.inflate(inflater, container, false)

        // Handle post submission
        binding.btnAddPost.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            Log.d("PostBottomSheetFragment", "Button clicked - Checking current user")

            if (currentUser != null) {
                val userId = currentUser.uid
                Log.d("PostBottomSheetFragment", "User ID: $userId")

                // Check if user profile exists in Firestore
                val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
                Log.d("PostBottomSheetFragment", "Fetching user data from Firestore")

                userRef.get().addOnSuccessListener { document ->
                    Log.d("PostBottomSheetFragment", "Firestore fetch success")

                    val userName = document.getString("username") ?: "Unknown User"
                    Log.d("PostBottomSheetFragment", "User Name: $userName")

                    val userImageUrl = document.getString("profileImageUrl")
                    val userImageUri = if (!userImageUrl.isNullOrEmpty()) {
                        Uri.parse(userImageUrl) // Load the real profile image (when available)
                    } else {
                        Uri.parse("android.resource://${requireContext().packageName}/${R.drawable.icons_user}") // Default icon
                    }
                    Log.d("PostBottomSheetFragment", "User Image URI: $userImageUri")

                    val caption = binding.etPostContent.text.toString().trim()
                    Log.d("PostBottomSheetFragment", "Caption: $caption")

                    if (caption.isNotEmpty() || imageUri != null) {
                        Log.d("PostBottomSheetFragment", "Adding post with caption: $caption and image: $imageUri")
                        viewModel.addPost(userName, userImageUri, imageUri, caption)
                        dismiss()
                    } else {
                        Log.d("PostBottomSheetFragment", "Caption and image are both empty")
                    }
                }.addOnFailureListener {
                    Log.d("PostBottomSheetFragment", "Firestore fetch failed, using default values")
                    val userName = "Unknown User"
                    val userImageUri = Uri.parse("android.resource://${requireContext().packageName}/${R.drawable.icons_user}")

                    val caption = binding.etPostContent.text.toString().trim()
                    if (caption.isNotEmpty() || imageUri != null) {
                        Log.d("PostBottomSheetFragment", "Adding post with default values")
                        viewModel.addPost(userName, userImageUri, imageUri, caption)
                        dismiss()
                    } else {
                        Log.d("PostBottomSheetFragment", "Caption and image are both empty")
                    }
                }
            } else {
                Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            }
        }


        // Handle image selection
        binding.btnUploadImage.setOnClickListener {
            openImagePicker()
        }

        return binding.root
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data // Handle image URI
            Log.d("PostBottomSheetFragment", "Image selected: $imageUri")
            binding.ivSelectedImage.setImageURI(imageUri)
            binding.ivSelectedImage.visibility = View.VISIBLE
        } else {
            Log.d("PostBottomSheetFragment", "No image selected or result is not OK")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }
}
