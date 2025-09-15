package com.example.skillexchangeapp.afterlogin.profilescreen

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.afterlogin.FullScreenImageActivity
import com.example.skillexchangeapp.afterlogin.profilescreen.addoffer.OfferAdapter
import com.example.skillexchangeapp.afterlogin.profilescreen.addoffer.RemoveOfferBottomSheetFragment
import com.example.skillexchangeapp.model.Offer
import com.example.skillexchangeapp.afterlogin.chat.ChatActivity
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val profileViewModel: ProfileViewModel by activityViewModels()

    private lateinit var profileImageView: ImageView
    private lateinit var fullNameTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var aboutMeTextView: TextView
    private lateinit var editProfileTextView: TextView

    private lateinit var recyclerViewSelectedItems: RecyclerView
    private lateinit var recyclerViewOffers: RecyclerView

    private lateinit var addOfferButton: ImageButton
    private lateinit var addOfferText: TextView
    private lateinit var chatButton: ImageButton

    private lateinit var offersTitle: TextView
    private lateinit var offersLayout: LinearLayout
    private lateinit var editLookingForLayout: LinearLayout
    private lateinit var buttonEditLookingFor: ImageButton

    private lateinit var offersAdapter: OfferAdapter
    private lateinit var resultSelectedCategoriesAdapter: ResultSelectedCategoriesAdapter

    private var viewingUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            viewingUserId = it.getString("userId")
            Log.d("ProfileFragment", "ViewingUserId from arguments: $viewingUserId")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_profile -> {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileImageView = view.findViewById(R.id.profileImage)
        fullNameTextView = view.findViewById(R.id.fullName)
        locationTextView = view.findViewById(R.id.locationText)
        aboutMeTextView = view.findViewById(R.id.aboutMeText)
        editProfileTextView = view.findViewById(R.id.editProfileText)

        recyclerViewSelectedItems = view.findViewById(R.id.recyclerViewSelectedItems)
        recyclerViewOffers = view.findViewById(R.id.recyclerViewOffers)

        addOfferButton = view.findViewById(R.id.buttonAddOffer)
        addOfferText = view.findViewById(R.id.textAddOffer)
        chatButton = view.findViewById(R.id.chatButton)

        offersLayout = view.findViewById(R.id.offers_Layout)
        offersTitle = view.findViewById(R.id.offersTitle)

        buttonEditLookingFor = view.findViewById(R.id.buttonEditLookingFor)
        editLookingForLayout = view.findViewById(R.id.editLookingForLayout)

        // --- Recycler για selected categories ---
        recyclerViewSelectedItems.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        resultSelectedCategoriesAdapter = ResultSelectedCategoriesAdapter(emptyList())
        recyclerViewSelectedItems.adapter = resultSelectedCategoriesAdapter

        // --- Recycler για offers ---
        recyclerViewOffers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        offersAdapter = OfferAdapter(
            onRemoveClick = { offer -> showRemoveOfferBottomSheet(offer) },
            showRemoveButton = profileViewModel.isCurrentUserProfile.value == true
        )
        recyclerViewOffers.adapter = offersAdapter

        val navigateToAddOffer = View.OnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_addNewOfferFragment)
        }
        addOfferButton.setOnClickListener(navigateToAddOffer)
        addOfferText.setOnClickListener(navigateToAddOffer)

        profileImageView.setOnClickListener {
            profileViewModel.profileImageUri.value?.takeIf { it.isNotEmpty() }?.let { base64 ->
                val intent = Intent(requireContext(), FullScreenImageActivity::class.java).apply {
                    putExtra("imageBase64", base64)
                }
                startActivity(intent)
            }
        }

        editProfileTextView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        buttonEditLookingFor.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_whatAreYouLookingForFragment)
        }

        chatButton.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            viewingUserId?.let { userId ->
                intent.putExtra("recipientId", userId)
                Log.d("ProfileFragment", "Starting ChatActivity with recipientId: $userId")
            } ?: Log.d("ProfileFragment", "Starting ChatActivity without recipientId")
            startActivity(intent)
        }

        Log.d("ProfileFragment", "Viewing id is: $viewingUserId")
        Log.d("ProfileFragment", "Logged in id is: ${profileViewModel.getLoggedInUserId()}")
        val targetUserId = viewingUserId ?: profileViewModel.getLoggedInUserId()
        targetUserId?.let {
            profileViewModel.setProfileIdAndFetchData(it)
            //profileViewModel.fetchSelectedCategoriesForUser(it)
            Log.d("ProfileFragment", "Fetching categories for user: $it")
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        profileViewModel.fullName.observe(viewLifecycleOwner) { fullName ->
            fullNameTextView.text = fullName
        }
        profileViewModel.location.observe(viewLifecycleOwner) { location ->
            locationTextView.text = location
        }
        profileViewModel.description.observe(viewLifecycleOwner) { aboutMe ->
            aboutMeTextView.text = aboutMe
        }
        profileViewModel.profileImageUri.observe(viewLifecycleOwner) { base64 ->
            if (!base64.isNullOrEmpty()) {
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                profileImageView.setImageBitmap(bitmap)
            } else {
                profileImageView.setImageResource(R.drawable.default_user_icon)
            }
        }
        profileViewModel.offers.observe(viewLifecycleOwner) { offers ->
            offersAdapter.submitList(offers)
            updateOffersVisibility()
        }
        profileViewModel.isCurrentUserProfile.observe(viewLifecycleOwner) { isCurrentUser ->
            addOfferButton.visibility = if (isCurrentUser) View.VISIBLE else View.GONE
            addOfferText.visibility = if (isCurrentUser) View.VISIBLE else View.GONE
            editProfileTextView.visibility = if (isCurrentUser) View.VISIBLE else View.GONE
            chatButton.visibility = if (isCurrentUser) View.GONE else View.VISIBLE
            offersAdapter.setShowRemoveButton(isCurrentUser)
            editLookingForLayout.visibility = if (isCurrentUser) View.VISIBLE else View.GONE
            updateOffersVisibility()
        }

        profileViewModel.selectedCategories.observe(viewLifecycleOwner) { categories ->
            Log.d("Categories", "Update categories in the adapter: $categories")
            resultSelectedCategoriesAdapter.updateCategories(categories)
        }
    }

    private fun updateOffersVisibility() {
        val isCurrentUser = profileViewModel.isCurrentUserProfile.value == true
        offersLayout.visibility = if (isCurrentUser) View.VISIBLE else View.GONE
    }

    private fun showRemoveOfferBottomSheet(offer: Offer) {
        val bottomSheet = RemoveOfferBottomSheetFragment.newInstance(
            offer,
            onRemoveConfirmed = { removedOffer ->
                profileViewModel.removeOffer(removedOffer)
            }
        )
        bottomSheet.show(parentFragmentManager, "RemoveOfferBottomSheet")
    }

    override fun onResume() {
        super.onResume()
        // Κάνει refresh με βάση τον logged-in user μέσα στο repository
        //SelectedCategoriesRepository.loadSelectedCategoriesFromFirestore()
    }
}

