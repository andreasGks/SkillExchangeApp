package com.example.skillexchangeapp.afterlogin

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.util.Log

class ProfileImageDialogFragment : DialogFragment() {

    interface ProfileImageDialogListener {
        fun onChooseProfilePicture()
        fun onSeeProfilePicture()
    }

    private var listener: ProfileImageDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Get the listener from the parent fragment instead of the activity
        val parentFragment = parentFragment
        Log.d("ProfileImageDialogFragment", "onAttach called, parentFragment: $parentFragment")
        if (parentFragment is ProfileImageDialogListener) {
            listener = parentFragment
            Log.d("ProfileImageDialogFragment", "Listener successfully set")
        } else {
            Log.e("ProfileImageDialogFragment", "Parent fragment is null or does not implement ProfileImageDialogListener")
            throw RuntimeException("$parentFragment must implement ProfileImageDialogListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Profile Picture")
            .setItems(arrayOf("Choose Profile Picture", "See Profile Picture")) { _, which ->
                when (which) {
                    0 -> listener?.onChooseProfilePicture()
                    1 -> listener?.onSeeProfilePicture()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
