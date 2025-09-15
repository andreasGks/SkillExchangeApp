package com.example.skillexchangeapp.afterlogin.searchscreen

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.model.UserSearch

class SearchAdapter(
    private var users: List<UserSearch>,
    private val onItemClick: (UserSearch) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val userImage: ImageView = itemView.findViewById(R.id.user_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_search, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = users[position]
        holder.userName.text = "${user.firstName} ${user.lastName}"

        if (user.profileImageUri.isNotEmpty()) {
            if (user.profileImageUri.startsWith("data:image")) {
                // Base64 image
                try {
                    val base64Image = user.profileImageUri.substringAfter(",")
                    val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    holder.userImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    holder.userImage.setImageResource(R.drawable.default_user_icon)
                }
            } else {
                // URL image
                Glide.with(holder.itemView.context)
                    .load(user.profileImageUri)
                    .placeholder(R.drawable.default_user_icon)
                    .into(holder.userImage)
            }
        } else {
            holder.userImage.setImageResource(R.drawable.default_user_icon)
        }

        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }

    override fun getItemCount() = users.size

    fun updateData(newUsers: List<UserSearch>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
