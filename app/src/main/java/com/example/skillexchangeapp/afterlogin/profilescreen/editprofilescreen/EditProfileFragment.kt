package com.example.skillexchangeapp.afterlogin.profilescreen.editprofilescreen

import EditProfileViewModel
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.afterlogin.FullScreenImageActivity
import com.example.skillexchangeapp.afterlogin.ImageTransformer
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.io.encoding.ExperimentalEncodingApi

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private val editProfileViewModel: EditProfileViewModel by activityViewModels()

    private lateinit var editTextName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var locationEditText: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var profilePhoto: CircleImageView

    private var tempImageUri: Uri? = null
    private var isProfileChanged = false

    private val imageTransformer by lazy {
        ImageTransformer(requireContext().contentResolver)
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextName = view.findViewById(R.id.firstName)
        editTextLastName = view.findViewById(R.id.lastName)
        editTextDescription = view.findViewById(R.id.description)
        locationEditText = view.findViewById(R.id.location)
        btnSaveChanges = view.findViewById(R.id.saveButton)
        profilePhoto = view.findViewById(R.id.profilePhoto)

        editProfileViewModel.firstName.observe(viewLifecycleOwner) { editTextName.setText(it) }
        editProfileViewModel.lastName.observe(viewLifecycleOwner) { editTextLastName.setText(it) }
        editProfileViewModel.description.observe(viewLifecycleOwner) {
            editTextDescription.setText(it)
        }
        editProfileViewModel.location.observe(viewLifecycleOwner) { locationEditText.setText(it) }

        editProfileViewModel.userProfileImage.observe(viewLifecycleOwner) { base64String ->
            if (!base64String.isNullOrEmpty()) {
                try {
                    lifecycleScope.launch(Dispatchers.IO) {
                        // val profileImageBitmap =
                        //     ImageTransformer.decodeBase64ToBitmap(base64String) ?: return@launch
                        val profileImageBitmap =
                            imageTransformer.decodeBase64ToBitmap(base64String) ?: return@launch
                        val originalProfileBitmap =
                            imageTransformer.upscale(profileImageBitmap, 400, 400)

                        withContext(Dispatchers.Main) {
                            profilePhoto.setImageBitmap(originalProfileBitmap ?: getDefaultImage())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("EditProfileFragment", "Error decoding Base64 image", e)
                    profilePhoto.setImageResource(R.drawable.default_user_icon)
                }
            } else profilePhoto.setImageResource(R.drawable.default_user_icon)
        }

        editProfileViewModel.loadUserProfile()

        btnSaveChanges.setOnClickListener {
            val fn = editTextName.text.toString().trim()
            val ln = editTextLastName.text.toString().trim()
            val desc = editTextDescription.text.toString().trim()
            val loc = locationEditText.text.toString().trim()

            if (fn.isBlank() || ln.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Name and Last Name can't be empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            editProfileViewModel.saveUserProfile(fn, ln, desc, loc)
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT)
                .show()

            isProfileChanged = false
            btnSaveChanges.isEnabled = false
        }

        profilePhoto.setOnClickListener {
            ProfilePhotoBottomSheetFragment(
                onSeePhotoClick = { openFullScreenImage() },
                onChangePhotoClick = { openGalleryForImage() }
            ).show(parentFragmentManager, null)
        }

        locationEditText.setOnClickListener {
            val intent = Intent(requireContext(), MapPickerActivity::class.java)
            mapIntentLauncher.launch(intent)
        }

        setFieldChangeListeners()
    }

    private val mapIntentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val selectedLat = result.data?.getDoubleExtra("selected_lat", 0.0)
                val selectedLng = result.data?.getDoubleExtra("selected_lng", 0.0)

                selectedLat?.let { lat ->
                    selectedLng?.let { lng ->
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        try {
                            val addresses = geocoder.getFromLocation(lat, lng, 1)
                            val addressName = addresses?.get(0)?.getAddressLine(0)
                                ?: "Lat: %.5f, Lng: %.5f".format(lat, lng)
                            locationEditText.setText(addressName)
                        } catch (e: Exception) {
                            locationEditText.setText("Lat: %.5f, Lng: %.5f".format(lat, lng))
                        }
                    }
                }
            }
        }

    private fun openGalleryForImage() {
        val pickIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
            }
        pickImageLauncher.launch(pickIntent)
    }

    private fun openFullScreenImage() {
        val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
        val imageString = tempImageUri?.toString() ?: editProfileViewModel.userProfileImage.value
        if (imageString.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No image to display", Toast.LENGTH_SHORT).show()
            return
        }
        intent.putExtra("IMAGE_URL_KEY", imageString)
        startActivity(intent)
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        // val profileImageBitmap = ImageTransformer.uriToBitmap(requireContext().contentResolver, uri) ?: return@launch
                        val profileImageBitmap = imageTransformer.uriToBitmap(uri) ?: return@launch
                        val downScaledBitmap = imageTransformer.downscale(profileImageBitmap, 400, 400)
                        val profileImageBase64 = imageTransformer.encodeBitmapToBase64(downScaledBitmap, 80)

                        editProfileViewModel.uploadProfilePhoto(profileImageBase64)
                    }
                    Glide.with(this).load(uri).into(profilePhoto)
                    tempImageUri = uri
                    Toast.makeText(
                        requireContext(),
                        "Photo selected successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    isProfileChanged = true
                    btnSaveChanges.isEnabled = true
                } else {
                    Toast.makeText(requireContext(), "Failed to get image", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private fun setFieldChangeListeners() {
        editTextName.addTextChangedListener { checkForChanges() }
        editTextLastName.addTextChangedListener { checkForChanges() }
        editTextDescription.addTextChangedListener { checkForChanges() }
        locationEditText.addTextChangedListener { checkForChanges() }
    }

    private fun checkForChanges() {
        val changed = editTextName.text.toString().trim() != editProfileViewModel.firstName.value ||
                editTextLastName.text.toString().trim() != editProfileViewModel.lastName.value ||
                editTextDescription.text.toString().trim() != editProfileViewModel.description.value ||
                locationEditText.text.toString().trim() != editProfileViewModel.location.value

        isProfileChanged = changed
        btnSaveChanges.isEnabled = isProfileChanged
    }

    private fun getDefaultImage() =
        BitmapFactory.decodeResource(resources, R.drawable.default_user_icon)
}
