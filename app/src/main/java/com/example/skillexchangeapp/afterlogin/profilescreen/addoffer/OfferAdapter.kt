package com.example.skillexchangeapp.afterlogin.profilescreen.addoffer

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.model.Offer

class OfferAdapter(
    private val onRemoveClick: (Offer) -> Unit,
    private val onItemClick: ((Offer) -> Unit)? = null,  // Νέο callback για κλικ στο item
    private var showRemoveButton: Boolean = true  // default true
) : ListAdapter<Offer, OfferAdapter.OfferViewHolder>(OfferDiffCallback()) {

    // Νέο: Setter για το showRemoveButton ώστε να αλλάζει δυναμικά
    fun setShowRemoveButton(show: Boolean) {
        showRemoveButton = show
        notifyDataSetChanged()  // Ανανεώνει όλα τα items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.offer_item, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val offerImageView: ImageView = itemView.findViewById(R.id.offerImageView)
        private val offerTitleTextView: TextView = itemView.findViewById(R.id.offerTitleTextView)
        private val offerDescTextView: TextView = itemView.findViewById(R.id.offerDescTextView)
        private val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        private val removeOfferButton: Button = itemView.findViewById(R.id.removeOfferButton)

        fun bind(offer: Offer) {
            offerTitleTextView.text = offer.title
            offerDescTextView.text = offer.description
            categoryTextView.text = "${offer.category} • ${offer.subCategory}"

            // Εμφάνιση/Κρυφή κουμπιού remove
            removeOfferButton.visibility = if (showRemoveButton) View.VISIBLE else View.GONE

            // Load Base64 image safely
            val base64Image = offer.imageUrl
            if (!base64Image.isNullOrBlank()) {
                try {
                    val decodedByteArray = Base64.decode(base64Image, Base64.DEFAULT)
                    val decodedBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)

                    Glide.with(itemView.context)
                        .load(decodedBitmap)
                        .placeholder(R.drawable.offer_icon)
                        .error(R.drawable.offer_icon)
                        .into(offerImageView)

                } catch (e: IllegalArgumentException) {
                    offerImageView.setImageResource(R.drawable.offer_icon)
                }
            } else {
                offerImageView.setImageResource(R.drawable.offer_icon)
            }

            // Remove button click
            removeOfferButton.setOnClickListener {
                onRemoveClick(offer)
            }

            // Κλικ στο ίδιο το item (ολόκληρο το view)
            itemView.setOnClickListener {
                onItemClick?.invoke(offer)
            }
        }
    }

    class OfferDiffCallback : DiffUtil.ItemCallback<Offer>() {
        override fun areItemsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem.offerId == newItem.offerId
        }

        override fun areContentsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem == newItem
        }
    }
}
