package com.example.skillexchangeapp.afterlogin.profilescreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.skillexchangeapp.afterlogin.FullScreenImageActivity
import com.example.skillexchangeapp.afterlogin.ProfileImageDialogFragment
import com.example.skillexchangeapp.R

class ProfileFragment : Fragment(R.layout.fragment_profile),
    ProfileImageDialogFragment.ProfileImageDialogListener {

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var profileImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var fullNameTextView: TextView
    private lateinit var editProfileView: TextView

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_profile -> {
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.action_profileFragment_to_editProfileFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImageView = view.findViewById(R.id.profileImage)
        userNameTextView = view.findViewById(R.id.userName)
        fullNameTextView = view.findViewById(R.id.fullName)
        editProfileView = view.findViewById(R.id.editProfileText)

        profileViewModel.fetchUserData()

        observeViewModel()

        editProfileView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }

    private fun observeViewModel() {
        profileViewModel.userName.observe(viewLifecycleOwner) { userName ->
            userNameTextView.text = userName
        }

        profileViewModel.fullName.observe(viewLifecycleOwner) { fullName ->
            fullNameTextView.text = fullName
        }

        profileViewModel.profileImageUri.observe(viewLifecycleOwner) { profileImageUri ->
            Glide.with(requireContext()).load(profileImageUri).into(profileImageView)
        }
    }

    override fun onChooseProfilePicture() {
        openGallery()
    }

    override fun onSeeProfilePicture() {
        val imageUrl = profileViewModel.profileImageUri.value
        if (!imageUrl.isNullOrEmpty()) {
            val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
            intent.putExtra("image_url", imageUrl)
            startActivity(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    profileViewModel.uploadProfileImage(uri)
                }
            }
        }
}


//class ProfileFragment : Fragment(R.layout.fragment_profile),
//    ProfileImageDialogFragment.ProfileImageDialogListener {
//
//    private val profileViewModel: ProfileViewModel by activityViewModels()
//
//    private lateinit var profileImageView: ImageView
//    private lateinit var userNameTextView: TextView
//    private lateinit var fullNameTextView: TextView
//    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
//
//    private lateinit var skillsRecyclerView: RecyclerView
//    private lateinit var upcomingSkillsRecyclerView: RecyclerView
//    private lateinit var skillsAdapter: SkillAdapter
//    private lateinit var upcomingSkillsAdapter: SkillAdapter
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Initialize Views
//        profileImageView = view.findViewById(R.id.profileImage)
//        userNameTextView = view.findViewById(R.id.userName)
//        fullNameTextView = view.findViewById(R.id.fullName)
//        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
//        skillsRecyclerView = view.findViewById(R.id.recyclerMySkills)
//        upcomingSkillsRecyclerView = view.findViewById(R.id.recyclerUpcomingSkills)
//
//        // Fetch data again to ensure fresh values
//        profileViewModel.fetchUserData()
//
//        // Observe and update user name
//        profileViewModel.userName.observe(viewLifecycleOwner) { userName ->
//            userNameTextView.text = userName
//        }
//
//        // Observe and update full name
//        profileViewModel.fullName.observe(viewLifecycleOwner) { fullName ->
//            fullNameTextView.text = fullName
//        }
//
//        // Observe and update profile image URI
//        profileViewModel.profileImageUri.observe(viewLifecycleOwner) { profileImageUri ->
//            if (!profileImageUri.isNullOrEmpty()) {
//                Glide.with(requireContext())
//                    .load(profileImageUri)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .placeholder(R.drawable.icons_user)
//                    .error(R.drawable.icons_user)
//                    .into(profileImageView)
//            } else {
//                profileImageView.setImageResource(R.drawable.icons_user)
//            }
//        }
//
//        // Set up RecyclerViews for skills
//        skillsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        skillsAdapter = SkillAdapter(mutableListOf(), ::onSkillClick, true)
//        skillsRecyclerView.adapter = skillsAdapter
//
//        upcomingSkillsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        upcomingSkillsAdapter = SkillAdapter(mutableListOf(), ::onSkillClick, false)
//        upcomingSkillsRecyclerView.adapter = upcomingSkillsAdapter
//
//        // Observe and update skills list
//        profileViewModel.skills.observe(viewLifecycleOwner) { skills ->
//            Log.d("ProfileFragment", "Skills updated: $skills")
//            skillsAdapter.updateSkills(skills)
//        }
//
//        // Observe and update upcoming skills list
//        profileViewModel.upcomingSkills.observe(viewLifecycleOwner) { upcomingSkills ->
//            Log.d("ProfileFragment", "Upcoming skills updated: $upcomingSkills")
//            upcomingSkillsAdapter.updateSkills(upcomingSkills)
//        }
//
//        swipeRefreshLayout.setOnRefreshListener {
//            profileViewModel.fetchUserData()
//            swipeRefreshLayout.isRefreshing = false
//        }
//
//        profileImageView.setOnClickListener {
//            val dialog = ProfileImageDialogFragment()
//            dialog.show(childFragmentManager, "ProfileImageDialog")
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        profileViewModel.fetchUserData() // Ensure fresh data when returning to profile
//    }
//
//    private fun onSkillClick(skill: String) {
//        // Handling skill edit or delete for both "My Skills" and "Upcoming Skills"
//        // If the skill is deletable (i.e., from "My Skills"), delete it
//        // If not, it could be for editing (add or modify skill)
//        if (skillsAdapter.isDeletable) {
//            profileViewModel.deleteSkill(skill)
//        } else {
//            // If it's not deletable, it is for editing, show the SkillDialogFragment to edit the skill
//            val dialog = SkillDialogFragment.newInstanceForEdit(skill) { updatedSkill ->
//                profileViewModel.updateSkill(skill, updatedSkill) // Assuming the profileViewModel handles updating the skill
//            }
//            dialog.show(childFragmentManager, "SkillDialog")
//        }
//    }
//
//    override fun onChooseProfilePicture() {
//        openGallery()
//    }
//
//    override fun onSeeProfilePicture() {
//        val imageUrl = profileViewModel.profileImageUri.value
//        if (!imageUrl.isNullOrEmpty()) {
//            val intent = Intent(requireContext(), FullScreenImageActivity::class.java)
//            intent.putExtra("image_url", imageUrl)
//            startActivity(intent)
//        }
//    }
//
//    // Launch Gallery to Pick an Image
//    private fun openGallery() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        galleryLauncher.launch(intent)
//    }
//
//    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            result.data?.data?.let { uri ->
//                profileViewModel.uploadProfileImage(uri) // Upload selected image
//            }
//        }
//    }
//}
//
