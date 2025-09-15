package com.example.skillexchangeapp.afterlogin.searchscreen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.afterlogin.profilescreen.CategoryData
import com.example.skillexchangeapp.model.UserSearch

class SearchFragment : Fragment() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: SearchAdapter
    private lateinit var filterButton: Button

    private val selectedFilters = mutableSetOf<String>()

    private val allFilters: List<String>
        get() {
            val subcategories = CategoryData
                .categoryData
                .values
                .flatten()
                .distinct()
                .sorted()
            return listOf("All") + subcategories
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        searchView = view.findViewById(R.id.search_view)
        recyclerView = view.findViewById(R.id.recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)
        filterButton = view.findViewById(R.id.open_filter_button)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.bottom = 16
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = 16
                }
            }
        })

        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        adapter = SearchAdapter(emptyList()) { user -> onUserSelected(user) }
        recyclerView.adapter = adapter

        viewModel.searchResults.observe(viewLifecycleOwner) { users ->
            adapter.updateData(users)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                recyclerView.visibility = View.VISIBLE
                showKeyboard()
            } else {
                recyclerView.visibility = View.GONE
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                clearSelectedFilters() // καθαρίζει παλιά filters
                performSearch(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                clearSelectedFilters() // καθαρίζει παλιά filters
                performSearch(newText ?: "")
                return true
            }
        })

        filterButton.setOnClickListener {
            val mainCategories = CategoryData.categoryData.keys.sorted()
            val filters = listOf("All") + mainCategories

            // Φτιάχνουμε νέα λίστα για να μην υπάρχουν προηγούμενες επιλογές
            val tempSelectedFilters = mutableSetOf<String>()

            val sheet = FilterBottomSheet(filters, tempSelectedFilters) { selected ->
                selectedFilters.clear()
                selectedFilters.addAll(selected)

                if (selectedFilters.contains("All")) {
                    viewModel.getAllUsers()
                } else {
                    viewModel.searchUsersByCategories(searchView.query.toString(), selectedFilters)
                }
            }
            sheet.show(parentFragmentManager, "FilterBottomSheet")
        }

        return view
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            if (selectedFilters.contains("All") || selectedFilters.isEmpty()) {
                viewModel.getAllUsers()
            } else {
                viewModel.searchUsersByCategories("", selectedFilters)
            }
        } else {
            if (selectedFilters.contains("All") || selectedFilters.isEmpty()) {
                viewModel.searchUsersWithFilter(query, emptyList())
            } else {
                viewModel.searchUsersByCategories(query, selectedFilters)
            }
        }
    }

    private fun clearSelectedFilters() {
        selectedFilters.clear()
    }

    private fun showKeyboard() {
        searchView.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)
    }

    private fun onUserSelected(user: UserSearch) {
        val action = SearchFragmentDirections.actionSearchFragmentToProfileFragment(user.userId)
        findNavController().navigate(action)
    }

    private fun clearSearchState() {
        searchView.setQuery("", false)
        searchView.clearFocus()
        recyclerView.visibility = View.GONE
        viewModel.searchResults.value = emptyList()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
        selectedFilters.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearSearchState()
    }
}
