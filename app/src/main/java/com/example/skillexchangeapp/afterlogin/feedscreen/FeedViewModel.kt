package com.example.skillexchangeapp.afterlogin.feedscreen

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.skillexchangeapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FeedViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("posts")
    private val auth = FirebaseAuth.getInstance()
    private val postRepository = PostRepository()

    private val _userFirstName = MutableLiveData<String>()
    val userFirstName: LiveData<String> get() = _userFirstName

    private val _userLastName = MutableLiveData<String>()
    val userLastName: LiveData<String> get() = _userLastName

    private val _userProfileImageBase64 = MutableLiveData<String>()
    val userProfileImageBase64: LiveData<String> get() = _userProfileImageBase64

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    init {
        loadUserData()
        loadPosts()
    }

    fun loadUserData() {
        val currentUser = auth.currentUser ?: run {
            Log.e("FeedViewModel", "No logged-in user found.")
            return
        }

        viewModelScope.launch {
            try {
                val document = firestore.collection("users").document(currentUser.uid).get().await()
                if (document.exists()) {
                    _userFirstName.value = document.getString("firstName") ?: "User"
                    _userLastName.value = document.getString("lastName") ?: ""
                    _userProfileImageBase64.value = document.getString("profilePhoto") ?: ""
                    Log.d("FeedViewModel", "User Data Loaded successfully")
                } else {
                    Log.e("FeedViewModel", "User document not found.")
                }
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error fetching user data", e)
            }
        }
    }

    fun addPost(userName: String, userImageUri: Uri, postImageUri: Uri?, caption: String) {
        val currentUser = auth.currentUser ?: run {
            Log.e("FeedViewModel", "No logged-in user found.")
            return
        }

        val newPost = Post(
            userId = currentUser.uid,
            userName = userName,
            userImageUri = userImageUri.toString(),
            postImageUri = postImageUri?.toString(),
            caption = caption
        )

        viewModelScope.launch {
            try {
                postRepository.addPost(newPost)
                loadPosts()
                Log.d("FeedViewModel", "Post added successfully.")
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error adding post", e)
            }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: run {
                    Log.e("FeedViewModel", "No logged-in user found.")
                    return@launch
                }

                val fetchedPosts = postRepository.getPostsByUserId(currentUser.uid)

                val enrichedPosts = fetchedPosts.map { post ->
                    try {
                        val userDoc = firestore.collection("users").document(post.userId).get().await()
                        if (userDoc.exists()) {
                            post.userName = userDoc.getString("firstName") ?: "Unknown"
                            post.userImageUri = userDoc.getString("profilePhoto") ?: ""
                        }
                    } catch (e: Exception) {
                        Log.e("FeedViewModel", "Error fetching user data for post", e)
                    }
                    post
                }

                _posts.value = enrichedPosts.reversed()

            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error loading posts", e)
            }
        }
    }

    fun deletePost(post: Post) {
        val currentUser = auth.currentUser ?: run {
            Log.e("FeedViewModel", "No logged-in user found.")
            return
        }

        viewModelScope.launch {
            try {
                postsCollection.document(post.id).delete().await()
                loadPosts()
                Log.d("FeedViewModel", "Post deleted successfully.")
            } catch (e: Exception) {
                Log.e("FeedViewModel", "Error deleting post", e)
            }
        }
    }
}
