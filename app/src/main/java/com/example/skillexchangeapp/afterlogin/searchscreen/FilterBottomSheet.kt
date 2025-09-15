package com.example.skillexchangeapp.afterlogin.searchscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skillexchangeapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet(
    private val filters: List<String>,
    private val selectedFilters: MutableSet<String>,
    private val onFilterSelected: (Set<String>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var filterRecyclerView: RecyclerView
    private lateinit var filterAdapter: FilterAdapter
    private lateinit var applyButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottom_sheet_filters, container, false)

        filterRecyclerView = view.findViewById(R.id.bottom_sheet_filter_recycler)
        filterRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        filterAdapter = FilterAdapter(filters, selectedFilters) { selected ->
            Log.d("FilterBottomSheet", "Filters updated in sheet: $selected")
        }




        filterRecyclerView.adapter = filterAdapter

        applyButton = view.findViewById(R.id.apply_filters_button)
        applyButton.setOnClickListener {
            Log.d("FilterBottomSheet", "Filters applied: $selectedFilters")
            onFilterSelected(selectedFilters)
            dismiss()
        }

        return view
    }
}
