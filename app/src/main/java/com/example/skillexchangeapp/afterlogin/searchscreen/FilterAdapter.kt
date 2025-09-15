package com.example.skillexchangeapp.afterlogin.searchscreen

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.skillexchangeapp.R

class FilterAdapter(
    private val filters: List<String>,
    private val selectedFilters: MutableSet<String>,
    private val onSelectionChanged: (Set<String>) -> Unit
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    init {
        Log.d("FilterAdapter", "Adapter created with selectedFilters: $selectedFilters")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_what_are_you_looking_for, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(filters[position])
    }

    override fun getItemCount(): Int = filters.size

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val filterText: TextView = itemView.findViewById(R.id.textCategory)
        private val checkIcon: ImageView = itemView.findViewById(R.id.checkIcon)
        private val cardLayout: View = itemView.findViewById(R.id.cardLayout)
        private val cardView: CardView = itemView.findViewById(R.id.cardViewItem)

        fun bind(filter: String) {
            // Ενημέρωση UI αρχικά
            updateUI(filter)

            cardLayout.setOnClickListener {
                val position = bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                if (selectedFilters.contains(filter)) selectedFilters.remove(filter)
                else selectedFilters.add(filter)

                notifyItemChanged(position) // ⬅ ενημέρωση UI
                onSelectionChanged(selectedFilters)
            }


        }

        private fun updateUI(filter: String) {
            val isSelected = selectedFilters.contains(filter)

            filterText.text = filter
            checkIcon.visibility = if (isSelected) View.VISIBLE else View.GONE
            filterText.setTextColor(
                if (isSelected) itemView.context.getColor(R.color.white)
                else itemView.context.getColor(R.color.black)
            )

            val backgroundColor = if (isSelected)
                itemView.context.getColor(R.color.color_primary)
            else
                itemView.context.getColor(R.color.color_background)

            cardView.setCardBackgroundColor(backgroundColor)

            Log.d("FilterAdapter", "updateUI -> $filter selected? $isSelected | selectedFilters: $selectedFilters")
        }
    }
}
