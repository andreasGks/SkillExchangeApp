package com.example.skillexchangeapp.afterlogin.chat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skillexchangeapp.R
import com.example.skillexchangeapp.model.UserProfile
import repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ChatActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: ChatAdapter
    private lateinit var recipientNameTextView: TextView

    private lateinit var recipientId: String
    private lateinit var currentUserId: String
    private lateinit var chatId: String

    private lateinit var viewModel: ChatViewModel
    private val userProfileRepository = UserProfileRepository()

    // Coroutine setup
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Find views
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        recipientNameTextView = findViewById(R.id.recipientNameTextView)

        // Init ViewModel
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        // Get recipientId from intent
        recipientId = intent.getStringExtra("recipientId") ?: run {
            finish()
            return
        }

        // Get current user id
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            finish()
            return
        }

        // Setup RecyclerView (adapter initialized after currentUserId is available)
        adapter = ChatAdapter(currentUserId)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = adapter

        // Create chatId based on both UIDs (sorted to be consistent)
        chatId = if (currentUserId < recipientId) {
            "${currentUserId}_$recipientId"
        } else {
            "${recipientId}_$currentUserId"
        }

        // Set data & start listening
        viewModel.setChatData(chatId, currentUserId)
        viewModel.startListeningForMessages()

        // Observe messages
        viewModel.messages.observe(this) { newMessages ->
            adapter.submitList(newMessages)
            messagesRecyclerView.scrollToPosition(newMessages.size - 1)
        }

        // Load recipient's name from UserProfileRepository using coroutine
        loadRecipientName()

        // Send button click
        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                viewModel.sendMessage(messageText)
                messageEditText.text.clear()
            }
        }
    }

    private fun loadRecipientName() {
        launch {
            val userProfile: UserProfile? = withContext(Dispatchers.IO) {
                userProfileRepository.getUserProfileById(recipientId)
            }
            if (userProfile != null) {
                val fullName = "${userProfile.firstname} ${userProfile.lastName}"
                recipientNameTextView.text = fullName
            } else {
                recipientNameTextView.text = "Άγνωστος Χρήστης"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopListening()
        job.cancel()
    }
}
