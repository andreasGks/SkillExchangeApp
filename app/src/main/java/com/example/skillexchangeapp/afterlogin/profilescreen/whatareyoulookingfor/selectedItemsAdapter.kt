//package com.example.skillexchangeapp.afterlogin.profilescreen.whatareyoulookingfor
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.skillexchangeapp.R
//
//class selectedItemsAdapter(
//    private var items: List<String>,
//    private val onItemClick: ((String) -> Unit)? = null
//) : RecyclerView.Adapter<selectedItemsAdapter.SelectedItemViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedItemViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_selected_category, parent, false)
//        return SelectedItemViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: SelectedItemViewHolder, position: Int) {
//        holder.bind(items[position])
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    fun updateCategories(newItems: List<String>) {
//        items = newItems
//        notifyDataSetChanged()  // Εδώ κάνεις notify ότι άλλαξαν τα δεδομένα
//    }
//
//    inner class SelectedItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameText)
//
//        fun bind(categoryName: String) {
//            categoryNameTextView.text = categoryName
//
//            itemView.setOnClickListener {
//                onItemClick?.invoke(categoryName)
//            }
//        }
//    }
//}
