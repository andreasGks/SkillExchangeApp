package com.example.skillexchangeapp.afterlogin.profilescreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillexchangeapp.model.UserSearch
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class WhatAreYouLookingForViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val selectedCategoriesRepository = SelectedCategoriesRepository()

    // Λίστα με όλες τις κύριες κατηγορίες
    private val _categories: MutableLiveData<Map<String, Boolean>> =
        MutableLiveData(CategoryData.categoryData.keys.associateWith { false })
    val categories: LiveData<Map<String, Boolean>> = _categories

    init {
        getInitialSelectedCategories() // asynchronously updates _categories
    }

    private fun getInitialSelectedCategories() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            val result = selectedCategoriesRepository.loadSelectedCategoriesWithResult(userId)
            result.getOrNull()?.let {
                val allCategories = CategoryData.categoryData.keys.toList()
                val selectedCategories = it
                _categories.value = getInitialSelectedCategories(allCategories, selectedCategories)
            }
        }
    }

    private fun getInitialSelectedCategories(
        allCategories: List<String>,
        selected: List<String>
    ): Map<String, Boolean> {
        return allCategories.associateWith { it in selected }
    }


    fun submit(){
        val selectedCategories = _categories.value?.filter { it.value }?.keys?.toList() ?: emptyList()
        selectedCategoriesRepository.setCategories(selectedCategories)
    }

    /* // LiveData από το Repository (δεν περνάμε πλέον userId – το repo το βρίσκει μόνο του)
     private val _selectedCategories: MutableLiveData<List<String>> = MutableLiveData<List<String>>(
         emptyList()
     )
     val selectedCategories: LiveData<List<String>> = _selectedCategories
 */

    // LiveData για τα αποτελέσματα του search
    private val _searchResults = MutableLiveData<List<UserSearch>>(emptyList())
    val searchResults: LiveData<List<UserSearch>> = _searchResults

    init {
        // Φορτώνουμε κατηγορίες από Firestore μόλις δημιουργηθεί το ViewModel
        selectedCategoriesRepository.loadSelectedCategoriesFromFirestore()
    }

    fun getSubcategoriesForCategory(category: String): List<String> {
        return CategoryData.categoryData[category] ?: emptyList()
    }

    fun toggleCategorySelection(category: String) {
        _categories.value = _categories.value?.toMutableMap()?.apply {
            this[category] = !(this[category] ?: false)
        }
    }

    fun setSelectedCategories(categories: List<String>) {
        selectedCategoriesRepository.setCategories(categories)
    }

    fun clearCategories() {
        selectedCategoriesRepository.clearCategories()
    }

    fun refreshCategories() {
        selectedCategoriesRepository.loadSelectedCategoriesFromFirestore()
    }

    /*  fun searchUsers(query: String) {
          viewModelScope.launch {
              val filters = selectedCategories.value ?: emptyList()
              val result = selectedCategoriesRepository.searchUsersWithFilter(query, filters)
              _searchResults.postValue(result)
          }
      }*/
}
