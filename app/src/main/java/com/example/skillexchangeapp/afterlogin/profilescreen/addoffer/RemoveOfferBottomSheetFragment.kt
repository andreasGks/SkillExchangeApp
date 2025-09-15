package com.example.skillexchangeapp.afterlogin.profilescreen.addoffer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.skillexchangeapp.databinding.FragmentRemoveOfferBottomSheetBinding
import com.example.skillexchangeapp.model.Offer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class RemoveOfferBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentRemoveOfferBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var offer: Offer? = null
    private var onRemoveConfirmed: ((Offer) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemoveOfferBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Πάρε το offer από τα arguments
        val offerJson = arguments?.getString("offer")
        offer = offerJson?.let { Gson().fromJson(it, Offer::class.java) }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.confirmButton.setOnClickListener {
            offer?.let {
                onRemoveConfirmed?.invoke(it)
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            offer: Offer,
            onRemoveConfirmed: (Offer) -> Unit
        ): RemoveOfferBottomSheetFragment {
            val fragment = RemoveOfferBottomSheetFragment()
            val args = Bundle()
            val offerJson = Gson().toJson(offer)
            args.putString("offer", offerJson)
            fragment.arguments = args
            fragment.onRemoveConfirmed = onRemoveConfirmed
            return fragment
        }
    }
}
