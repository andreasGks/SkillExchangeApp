package com.example.skillexchangeapp.afterlogin.profilescreen

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.skillexchangeapp.R

class DeleteConfirmationDialogFragment(private val onDeleteConfirmed: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = android.app.AlertDialog.Builder(requireContext())

        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_delete_confirmation, null)

        builder.setView(view)
            .setTitle("Are you sure?")
            .setPositiveButton("Yes") { _, _ ->
                onDeleteConfirmed() // Trigger the delete operation
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Close the dialog without deleting
            }

        return builder.create()
    }
}
