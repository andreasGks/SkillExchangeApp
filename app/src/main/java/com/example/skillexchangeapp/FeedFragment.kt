//package com.example.skillexchangeapp
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//
//class FeedFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_feed, container, false)
//
//        // Find RecyclerView
//        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
//
//        // Create sample post list
//        val samplePosts = listOf(
//            "First Post: Welcome to Skill Exchange!",
//            "Second Post: Anyone looking for a Kotlin tutor?",
//            "Third Post: Need help with Firebase setup?"
//        )
//
//        // Set up RecyclerView
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.adapter = FeedAdapter(samplePosts)
//
//        return view
//    }
//}
package com.example.skillexchangeapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FeedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var fabAddPost: FloatingActionButton

    private var samplePosts = mutableListOf(
        "First Post: Welcome to Skill Exchange!",
        "Second Post: Anyone looking for a Kotlin tutor?",
        "Third Post: Need help with Firebase setup?"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        // Find views
        recyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        fabAddPost = view.findViewById(R.id.fabAddPost)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = FeedAdapter(samplePosts)

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            refreshFeed()
        }

        // Set up Floating Action Button
        fabAddPost.setOnClickListener {
            Toast.makeText(requireContext(), "Add Post Clicked", Toast.LENGTH_SHORT).show()
            Log.d("FeedFragment", "Floating Action Button clicked!")
        }

        return view
    }

    // Simulated refresh function
    private fun refreshFeed() {
        // Simulate adding new post (replace this with actual API/database fetching logic)
        samplePosts.add(0, "New Post: Refreshed at ${System.currentTimeMillis()}")
        recyclerView.adapter?.notifyDataSetChanged()

        // Hide refresh indicator after 1 second
        swipeRefreshLayout.isRefreshing = false
    }
}
