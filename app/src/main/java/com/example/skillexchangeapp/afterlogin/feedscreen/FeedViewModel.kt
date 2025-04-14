package com.example.skillexchangeapp.afterlogin.feedscreen

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skillexchangeapp.afterlogin.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FeedViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("posts")
    private val auth = FirebaseAuth.getInstance()

    private val _userFirstName = MutableLiveData<String>()
    val userFirstName: LiveData<String> get() = _userFirstName

    private val _userLastName = MutableLiveData<String>()
    val userLastName: LiveData<String> get() = _userLastName

    private val _userProfileImageUri = MutableLiveData<String>()
    val userProfileImageUri: LiveData<String> get() = _userProfileImageUri

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    init {
        loadUserData()
        loadPosts()
    }

    fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("FeedViewModel", "No logged-in user found.")
            return
        }

        firestore.collection("users").document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val firstName = document.getString("firstName") ?: "User"
                    val lastName = document.getString("lastName") ?: ""
                    val profileImageUri = document.getString("profileImageUri") ?: ""

                    _userFirstName.postValue(firstName)
                    _userLastName.postValue(lastName)
                    _userProfileImageUri.postValue(profileImageUri)

                    Log.d("FeedViewModel", "User Data Loaded: $firstName $lastName, Image: $profileImageUri")
                } else {
                    Log.e("FeedViewModel", "User document not found.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FeedViewModel", "Error fetching user data", e)
            }
    }

    fun addPost(userName: String, userImageUri: Uri, postImageUri: Uri?, caption: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
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

        postsCollection.add(newPost.toMap())
            .addOnSuccessListener {
                loadPosts()
            }
            .addOnFailureListener { e ->
                Log.e("FeedViewModel", "Error adding post", e)
            }
    }

    fun loadPosts() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("FeedViewModel", "No logged-in user found.")
            return
        }

        postsCollection.whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                val tempPosts = mutableListOf<Post>()
                val tasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

                for (document in documents) {
                    val post = Post.fromMap(document.data).copy(id = document.id)

                    val userTask = firestore.collection("users").document(post.userId)
                        .get()
                        .addOnSuccessListener { userDoc ->
                            if (userDoc.exists()) {
                                post.userName = userDoc.getString("firstName") ?: "Unknown"
                                post.userImageUri = userDoc.getString("profileImageUri") ?: ""
                            }
                        }

                    tasks.add(userTask)
                    tempPosts.add(post)
                }

                // Wait until all user fetches are done
                com.google.android.gms.tasks.Tasks.whenAllComplete(tasks)
                    .addOnSuccessListener {
                        _posts.value = tempPosts.reversed()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FeedViewModel", "Error loading posts", e)
            }
    }

    fun deletePost(post: Post) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("FeedViewModel", "No logged-in user found.")
            return
        }

        postsCollection.document(post.id)
            .delete()
            .addOnSuccessListener {
                loadPosts()
                Log.d("FeedViewModel", "Post deleted successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("FeedViewModel", "Error deleting post", e)
            }
    }
}
