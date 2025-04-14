package com.example.skillexchangeapp.afterlogin.feedscreen

import android.content.Intent
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
import com.example.skillexchangeapp.afterlogin.FullScreenImageActivity

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

        if (post.userImageUri.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(post.userImageUri)
                .circleCrop()
                .into(holder.userImage)
        } else {
            holder.userImage.setImageResource(R.drawable.icons_user)
        }

        holder.userImage.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, FullScreenImageActivity::class.java)
            intent.putExtra("image_url", post.userImageUri)
            context.startActivity(intent)
        }

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
            holder.postImage.setImageResource(R.drawable.empty_box)
        }

        holder.postImage.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, FullScreenImageActivity::class.java)
            intent.putExtra("image_url", post.postImageUri)
            context.startActivity(intent)
        }

        holder.caption.text = post.caption

        holder.deletePost.setOnClickListener {
            val dialog = DeleteConfirmationDialogFragment {
                onDeletePost(post)
            }
            dialog.show(
                (holder.itemView.context as AppCompatActivity).supportFragmentManager,
                "DeleteConfirmationDialog"
            )
        }
    }


    override fun getItemCount(): Int = posts.size

    // Method to update the posts list
    fun updatePosts(updatedPosts: List<Post>) {
        this.posts = updatedPosts
        notifyDataSetChanged()
    }
}

