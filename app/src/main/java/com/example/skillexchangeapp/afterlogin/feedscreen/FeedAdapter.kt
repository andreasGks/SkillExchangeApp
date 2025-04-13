package com.example.skillexchangeapp.afterlogin.feedscreen

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skillexchangeapp.afterlogin.profilescreen.DeleteConfirmationDialogFragment
import com.example.skillexchangeapp.afterlogin.Post
import com.example.skillexchangeapp.R

class FeedAdapter(
    private var posts: List<Post>,
    private val onDeletePost: (Post) -> Unit // Callback to delete post
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    class FeedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.userName)
        val userImage: ImageView = view.findViewById(R.id.userImage)
        val postImage: ImageView = view.findViewById(R.id.postImage)
        val caption: TextView = view.findViewById(R.id.caption)
        val deletePost: ImageView = view.findViewById(R.id.deletePost) // Add delete button reference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val post = posts[position]
        holder.userName.text = post.userName

        // Load the user image with Glide and round it
        if (post.userImageUri.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(post.userImageUri)
                .circleCrop() // Make the image round
                .into(holder.userImage)
        } else {
            holder.userImage.setImageResource(R.drawable.icons_user) // Default image if no URI
        }

        // Handle the post image
        if (post.postImageUri != null) {
            try {
                holder.postImage.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(post.postImageUri)
                    .into(holder.postImage)
            } catch (e: Exception) {
                Log.e("FeedAdapter", "Error loading post image", e)
            }
        } else {
            holder.postImage.visibility = View.GONE
            holder.postImage.setImageResource(R.drawable.empty_box) // Default image if no post image
        }

        // Set the caption text
        holder.caption.text = post.caption

        // Handle the delete button click
        holder.deletePost.setOnClickListener {
            // Show confirmation dialog before deleting the post
            val dialog = DeleteConfirmationDialogFragment {
                // If confirmed, trigger the delete operation via the callback
                onDeletePost(post)
            }
            dialog.show((holder.itemView.context as AppCompatActivity).supportFragmentManager, "DeleteConfirmationDialog")
        }
    }

    override fun getItemCount(): Int = posts.size

    // Method to update the posts list
    fun updatePosts(updatedPosts: List<Post>) {
        this.posts = updatedPosts
        notifyDataSetChanged()
    }
}

