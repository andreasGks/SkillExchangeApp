package com.example.skillexchangeapp.afterlogin.profilescreen.editprofilescreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.skillexchangeapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfilePhotoBottomSheetFragment(
    private val onSeePhotoClick: () -> Unit,
    private val onChangePhotoClick: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_options_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val seePhoto = view.findViewById<TextView>(R.id.seePhoto)
        val changePhoto = view.findViewById<TextView>(R.id.changePhoto)

        seePhoto.setOnClickListener {
            onSeePhotoClick()
            dismiss()
        }


        changePhoto.setOnClickListener {
            onChangePhotoClick()
            dismiss()
        }
    }
}
