package com.example.skillexchangeapp.afterlogin.profilescreen

import android.util.Log
import androidx.lifecycle.*
import com.example.skillexchangeapp.model.Offer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import repository.OffersRepository

class ProfileViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val offersRepository = OffersRepository()
    private val selectedCategoriesRepository = SelectedCategoriesRepository()

    private val _fullName = MutableLiveData<String>()
    val fullName: LiveData<String> = _fullName

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _profileImageUri = MutableLiveData<String>()
    val profileImageUri: LiveData<String> = _profileImageUri

    private val _offers = MutableLiveData<List<Offer>>(emptyList())
    val offers: LiveData<List<Offer>> = _offers

    private val _isCurrentUserProfile = MutableLiveData<Boolean>()
    val isCurrentUserProfile: LiveData<Boolean> = _isCurrentUserProfile

    val currentUserId = MutableLiveData<String>()
    var currentProfileId: String? = null

    private val _showOffersSection = MutableLiveData<Boolean>(false)
    val showOffersSection: LiveData<Boolean> = _showOffersSection

    // Αντί για SharedProfileLookingForManager → τα κρατάμε εδώ
    private val _selectedCategories = MutableLiveData<List<String>>(emptyList())
    val selectedCategories: LiveData<List<String>> = _selectedCategories

    init {
        currentUserId.value = auth.currentUser?.uid
    }

    fun setProfileIdAndFetchData(profileId: String) {
        currentProfileId = profileId
        _isCurrentUserProfile.value = (currentUserId.value == profileId)
        fetchUserData(profileId)
        fetchUserOffers(profileId)
        fetchSelectedCategoriesForUser2(profileId)
    }

    private fun fetchUserData(profileId: String) {
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(profileId).get().await()
                _fullName.value = doc.getString("fullName") ?: "No name"
                _location.value = doc.getString("location") ?: "No location"
                _description.value = doc.getString("description") ?: "No description"
                _profileImageUri.value = doc.getString("profilePhoto") ?: ""
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching user data", e)
            }
        }
    }

    private fun fetchUserOffers(profileId: String) {
        viewModelScope.launch {
            try {
                val offerList = offersRepository.getOffersByUser(profileId)
                _offers.value = offerList
                updateOffersSectionVisibility()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching offers", e)
                _offers.value = emptyList()
                updateOffersSectionVisibility()
            }
        }
    }

    private fun updateOffersSectionVisibility() {
        val hasOffers = _offers.value?.isNotEmpty() == true
        val isCurrentUser = _isCurrentUserProfile.value == true
        _showOffersSection.value = isCurrentUser || hasOffers
    }

    fun updateProfileImageBase64(base64Image: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("users").document(userId)
                    .update("profilePhoto", base64Image)
                    .await()
                withContext(Dispatchers.Main) {
                    fetchUserData(userId)
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to update profile photo", e)
            }
        }
    }

    fun removeOffer(offer: Offer) {
        val offerId = offer.offerId
        if (offerId.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            val success = offersRepository.deleteOffer(offerId)
            if (success) {
                currentProfileId?.let { fetchUserOffers(it) }
            }
        }
    }

    fun getLoggedInUserId(): String? = auth.currentUser?.uid

    fun updateUserDescription(newDescription: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("users").document(userId)
                    .update("description", newDescription)
                    .await()
                withContext(Dispatchers.Main) {
                    fetchUserData(userId)
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to update description", e)
            }
        }
    }

    fun updateUserLocation(newLocation: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("users").document(userId)
                    .update("location", newLocation)
                    .await()
                withContext(Dispatchers.Main) {
                    fetchUserData(userId)
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to update location", e)
            }
        }
    }

    /** ---------- LOOKING FOR MANAGEMENT ---------- */

   /* fun fetchSelectedCategoriesForUser(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").document(userId).get().await()
                val categories = snapshot.get("lookingFor") as? List<String> ?: emptyList()
                _selectedCategories.value = categories
                Log.d("ProfileViewModel", "Fetched lookingFor categories: $categories")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching selected categories", e)
            }
        }
    }*/

    fun fetchSelectedCategoriesForUser2(userId: String){
        viewModelScope.launch {
            selectedCategoriesRepository.getSelectedCategoriesForProfile(userId) { categories ->
                Log.d("Categories", "Fetching categories from repo : $categories")
                _selectedCategories.value = categories
            }
        }
    }

   /* fun toggleCategory(category: String) {
        val currentList = _selectedCategories.value.toMutableList()
        if (currentList.contains(category)) {
            currentList.remove(category)
        } else {
            currentList.add(category)
        }
        _selectedCategories.value = currentList
        saveSelectedCategoriesToFirestore(currentList)
    }*/

   /* private fun saveSelectedCategoriesToFirestore(categories: List<String>) {
        val userId = getLoggedInUserId() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("users").document(userId)
                    .update("lookingFor", categories)
                    .await()
                Log.d("ProfileViewModel", "Saved categories to Firestore: $categories")
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saving categories", e)
            }
        }
    }*/
}
