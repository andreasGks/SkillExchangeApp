package com.example.skillexchangeapp.afterlogin.profilescreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skillexchangeapp.databinding.FragmentWhatAreYouLookingForBinding
import com.google.firebase.auth.FirebaseAuth

class WhatAreYouLookingForFragment : Fragment() {
    private var _binding: FragmentWhatAreYouLookingForBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WhatAreYouLookingForViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWhatAreYouLookingForBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Αν δεν υπάρχει logged-in user, εμφανίζουμε μήνυμα και κάνουμε back
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Δεν υπάρχει συνδεδεμένος χρήστης", Toast.LENGTH_SHORT)
                .show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            viewModel.submit()
            Toast.makeText(requireContext(), "Your categories have been updated!", Toast.LENGTH_SHORT).show()
        }


        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        val adapter = WhatAreYouLookingForAdapter(
            categories = viewModel.categories.value ?: emptyMap(),
            onCategoryClick = { category ->
                Log.e("WhatAreYouLookingFor", "Category clicked: $category")
                viewModel.toggleCategorySelection(category)
            }
        )
        binding.recyclerViewCategories.adapter = adapter

        // Παρακολουθούμε τις λίστες από το ViewModel
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.updateCategories(categories)
        }

        /*  viewModel.selectedCategories.observe(viewLifecycleOwner) { selected ->
              adapter.setSelectedCategories(selected)
          }*/

        // Αν θέλεις να προσθέσεις reset κουμπί:
        // binding.clearButton.setOnClickListener {
        //     viewModel.clearCategories()
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
