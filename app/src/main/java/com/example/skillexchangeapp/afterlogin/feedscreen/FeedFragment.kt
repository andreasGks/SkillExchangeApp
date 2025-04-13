package com.example.skillexchangeapp.afterlogin.feedscreen

import android.os.Bundle
import android.util.Log  // ✅ Added import for Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.skillexchangeapp.afterlogin.PostBottomSheetFragment
import com.example.skillexchangeapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FeedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddPost: FloatingActionButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: FeedAdapter

    private lateinit var userWelcomeMessage: TextView
    private lateinit var userProfileImage: ImageView

    private val viewModel: FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        fabAddPost = view.findViewById(R.id.fabAddPost)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        userWelcomeMessage = view.findViewById(R.id.userWelcomeMessage)
        userProfileImage = view.findViewById(R.id.userProfileImage)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter
        adapter = FeedAdapter(emptyList()) { post ->
            viewModel.deletePost(post)  // Delete post when clicked
        }

        recyclerView.adapter = adapter

        // Observe posts LiveData and update the adapter
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts)
            swipeRefreshLayout.isRefreshing = false
        }

        // Observe user profile image and update ImageView
        viewModel.userProfileImageUri.observe(viewLifecycleOwner) { imageUrl ->
            if (imageUrl.isNotEmpty()) {
                Glide.with(requireContext()).load(imageUrl).into(userProfileImage)
            }
        }

        // Handle pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPosts()  // Refresh posts when user pulls to refresh
        }

        // Open PostBottomSheetFragment to add new post
        fabAddPost.setOnClickListener {
            val bottomSheet = PostBottomSheetFragment()
            bottomSheet.show(parentFragmentManager, "PostBottomSheetFragment")
        }

        return view
    }

    // Move LiveData observations to onViewCreated() method
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userFirstName.observe(viewLifecycleOwner) { updateUserName() }
        viewModel.userLastName.observe(viewLifecycleOwner) { updateUserName() }
    }

    // Function to update the user welcome message and log the update
    private fun updateUserName() {
        val firstName = viewModel.userFirstName.value ?: "User"
        val lastName = viewModel.userLastName.value ?: ""
        userWelcomeMessage.text = "Hello, $firstName $lastName!"

        Log.d("FeedFragment", "Updated Welcome Message: Hello, $firstName $lastName")
    }
}