package com.example.skillexchangeapp.afterlogin.searchscreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillexchangeapp.model.UserSearch
import kotlinx.coroutines.launch
import repository.OffersRepository
import repository.SearchRepository

class SearchViewModel(
    private val repository: SearchRepository = SearchRepository(),
    private val offersRepository: OffersRepository = OffersRepository()
) : ViewModel() {

    val searchResults = MutableLiveData<List<UserSearch>>()
    val isLoading = MutableLiveData<Boolean>()

    // Search by name
    fun searchUsers(query: String) {
        isLoading.value = true
        viewModelScope.launch {
            val results = repository.searchUsers(query)
            searchResults.value = results
            isLoading.value = false
        }
    }

    // Get all users
    fun getAllUsers() {
        isLoading.value = true
        viewModelScope.launch {
            val results = repository.getAllUsers()
            searchResults.value = results
            isLoading.value = false
        }
    }

    // Search users with filters
    fun searchUsersWithFilter(query: String, filters: List<String>) {
        isLoading.value = true
        viewModelScope.launch {
            val results = repository.searchUsersWithFilter(query, filters)
            searchResults.value = results
            isLoading.value = false
        }
    }

    // NEW: Search users by offer categories + optional name query
    fun searchUsersByCategories(query: String = "", selectedCategories: Set<String>) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                // Παίρνουμε μοναδικούς userIds που έχουν offers στις selected categories
                val userIds = offersRepository.getUserIdsByCategories(selectedCategories.toList())
                Log.d("SearchViewModel", "UserIds from categories $selectedCategories: $userIds")

                // Παίρνουμε όλους τους users
                val allUsers = repository.getAllUsers()

                // Φιλτράρουμε τους users βάσει userIds και query
                val users = allUsers.filter { user ->
                    user.userId in userIds &&
                            (query.isEmpty() ||
                                    user.firstName.contains(query, true) ||
                                    user.lastName.contains(query, true))
                }

                Log.d("SearchViewModel", "Filtered users: ${users.map { it.firstName + " " + it.lastName }}")
                searchResults.value = users
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error searching users by categories", e)
                searchResults.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }
}
