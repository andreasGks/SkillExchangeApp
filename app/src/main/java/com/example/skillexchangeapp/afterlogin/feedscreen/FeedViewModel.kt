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
        loadUserData()  // Fetch user info when ViewModel is created
        loadPosts()     // Fetch posts
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

                    _userFirstName.postValue(firstName)  // Use postValue() instead of value
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
            .addOnSuccessListener { documentRef ->
                // Firestore generates an ID for the document
                val postId = documentRef.id  // Get the auto-generated ID
                newPost.copy(id = postId) // Update the Post object with the generated ID
                loadPosts() // Refresh posts after adding a new post
            }
            .addOnFailureListener { e -> Log.e("FeedViewModel", "Error adding post", e) }
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
                val postList = documents.map { document ->
                    val post = Post.fromMap(document.data)
                    // Fetch the user details based on userId
                    firestore.collection("users").document(post.userId)
                        .get()
                        .addOnSuccessListener { userDocument ->
                            if (userDocument.exists()) {
                                val userName = userDocument.getString("name") ?: "Unknown User"
                                val userImageUri = userDocument.getString("profileImageUri") ?: ""
                                post.userName = userName
                                post.userImageUri = userImageUri
                            }
                        }
                    post.copy(id = document.id)  // Set Firestore document ID
                }
                _posts.value = postList.reversed() // Show newest posts first
            }
            .addOnFailureListener { e -> Log.e("FeedViewModel", "Error loading posts", e) }
    }

    fun deletePost(post: Post) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("FeedViewModel", "No logged-in user found.")
            return
        }

        // Ensure that post.id is a valid document ID and not just 'posts'
        postsCollection.document(post.id)  // This should point to a specific post document
            .delete()
            .addOnSuccessListener {
                loadPosts() // Refresh posts after deletion
                Log.d("FeedViewModel", "Post deleted successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("FeedViewModel", "Error deleting post", e)
            }
    }


}
