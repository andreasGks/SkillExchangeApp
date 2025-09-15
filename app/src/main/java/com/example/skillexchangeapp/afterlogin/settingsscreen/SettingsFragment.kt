package com.example.skillexchangeapp.afterlogin.settingsscreen

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.afterlogin.profilescreen.editprofilescreen.EditProfileFragment
import com.example.skillexchangeapp.beforelogin.login.MainActivity

class SettingsFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val btnLogout: Button = view.findViewById(R.id.btnLogout)


        btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return view
    }

    private fun showLogoutConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to log out?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                settingsViewModel.logout()
                navigateToMainScreen()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        dialogBuilder.create().show()
    }

    private fun navigateToMainScreen() {
        requireActivity().finish() // Close AfterLoginActivity
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
