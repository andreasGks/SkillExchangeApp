package com.example.skillexchangeapp.afterlogin.profilescreen

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.skillexchangeapp.R

class WhatAreYouLookingForAdapter(
    private var categories: Map<String, Boolean>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<WhatAreYouLookingForAdapter.VH>() {

    fun updateCategories(newCategories: Map<String, Boolean>) {
        categories = newCategories
        Log.d("WhatAreYouLookingFor", "setSelectedCategories: $categories")
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val text: TextView = view.findViewById(R.id.textCategory)
        private val checkIcon: ImageView = view.findViewById(R.id.checkIcon)
        private val cardViewItem: CardView = view.findViewById(R.id.cardViewItem)

        fun bind(item: String, isSelected: Boolean) {
            Log.d(
                "WhatAreYouLookingFor",
                "bind: $item selected=$isSelected"
            )
            text.text = item
            updateSelection(item, isSelected)

            cardViewItem.setOnClickListener {
                Log.d("WhatAreYouLookingFor", "CLICK on $item")
                onCategoryClick(item)
            }
        }

        private fun updateSelection(item: String, isSelected: Boolean) {
            if (isSelected) {
                Log.d("WhatAreYouLookingFor", "updateSelection: $item -> SELECTED")
                text.setBackgroundResource(R.color.color_mint_pressed)
                checkIcon.visibility = View.VISIBLE
            } else {
                Log.d("WhatAreYouLookingFor", "updateSelection: $item -> NOT selected")
                text.setBackgroundResource(R.color.color_mint)
                checkIcon.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_what_are_you_looking_for, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = categories.keys.elementAt(position)
        val isSelected = categories[item] == true
        holder.bind(item, isSelected)
    }
}
