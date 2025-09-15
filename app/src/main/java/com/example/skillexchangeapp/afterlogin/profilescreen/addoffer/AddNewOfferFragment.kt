package com.example.skillexchangeapp.afterlogin.profilescreen.addoffer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.afterlogin.ImageTransformer
import com.example.skillexchangeapp.model.Offer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AddNewOfferFragment : Fragment(R.layout.fragment_add_new_offer) {

    private lateinit var editField: EditText
    private lateinit var editDesc: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var confirmButton: Button
    private lateinit var addNewOffersViewModel: AddNewOffersViewModel
    private lateinit var categoryAutoComplete: AutoCompleteTextView
    private lateinit var subCategoryAutoComplete: AutoCompleteTextView
    private lateinit var imageTransformer: ImageTransformer  // μεταφέρθηκε εδώ

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            imagePreview.setImageURI(selectedImageUri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editField = view.findViewById(R.id.editSkillField)
        editDesc = view.findViewById(R.id.editSkillDescription)
        imagePreview = view.findViewById(R.id.imageSkillPreview)
        confirmButton = view.findViewById(R.id.buttonConfirmSkill)
        categoryAutoComplete = view.findViewById(R.id.autoCompleteCategory)
        subCategoryAutoComplete = view.findViewById(R.id.autoCompleteSubCategory)

        // initialize εδώ, που έχουμε σίγουρα context
        imageTransformer = ImageTransformer(requireContext().contentResolver)


        addNewOffersViewModel = ViewModelProvider(this).get(AddNewOffersViewModel::class.java)
        setupSkillCategories(view)


        imagePreview.setOnClickListener {
            openImagePicker()
        }

        confirmButton.setOnClickListener {
            val field = editField.text.toString().trim()
            val desc = editDesc.text.toString().trim()
            val category = categoryAutoComplete.text.toString().trim()
            val subCategory = subCategoryAutoComplete.text.toString().trim()

            if (field.isEmpty() || desc.isEmpty() || category.isEmpty() || subCategory.isEmpty()) {
                Toast.makeText(requireContext(), "Fill in all fields!", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val base64Image = selectedImageUri?.let { uri ->
                        getBase64FromUri(uri)
                    } ?: ""

                    val offer = Offer(
                        title = field,
                        category = category,
                        subCategory = subCategory,
                        description = desc,
                        imageUrl = base64Image
                    )

                    addNewOffersViewModel.addNewOffer(offer)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Skill added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setupSkillCategories(view: View) {
        val autoCompleteCategory = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteCategory)
        val autoCompleteSubCategory = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteSubCategory)

        val categories = addNewOffersViewModel.categoryMap.keys.toList()
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        autoCompleteCategory.setAdapter(categoryAdapter)

        autoCompleteCategory.setOnItemClickListener { parent, _, position, _ ->
            val selectedCategory = parent.getItemAtPosition(position).toString()
            val subcategories = addNewOffersViewModel.categoryMap[selectedCategory] ?: emptyList()
            val subCategoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, subcategories)
            autoCompleteSubCategory.setAdapter(subCategoryAdapter)
            subCategoryAutoComplete.setText("")
            subCategoryAutoComplete.showDropDown()
        }

        autoCompleteCategory.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) autoCompleteCategory.showDropDown()
        }
        autoCompleteSubCategory.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) autoCompleteSubCategory.showDropDown()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun getBase64FromUri(uri: Uri): String {
        val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(uri))
        val upscaledBitmap = imageTransformer.upscale(bitmap, 400, 400)
        return encodeImageToBase64(upscaledBitmap)
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
