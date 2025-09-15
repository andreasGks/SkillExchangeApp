package com.example.skillexchangeapp.afterlogin.profilescreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.model.UserProfile

class ResultSelectedCategoriesAdapter(
    private var categories: List<String>
) : RecyclerView.Adapter<ResultSelectedCategoriesAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val textCategory: TextView = v.findViewById(R.id.textCategory)

        fun bind(item: String) {
            textCategory.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_category, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    fun updateCategories(newCategories: List<String>) {
        categories = newCategories
        notifyDataSetChanged()
    }
}
