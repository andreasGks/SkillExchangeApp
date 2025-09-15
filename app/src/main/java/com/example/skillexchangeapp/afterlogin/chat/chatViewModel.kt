package com.example.skillexchangeapp.afterlogin.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.skillexchangeapp.model.Message
import com.google.firebase.firestore.ListenerRegistration
import repository.ChatRepository

class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val chatRepo = ChatRepository()

    private var chatId: String? = null
    private var senderId: String? = null

    private var messagesListener: ListenerRegistration? = null

    fun setChatData(chatId: String, senderId: String) {
        this.chatId = chatId
        this.senderId = senderId
    }

    fun startListeningForMessages() {
        chatId?.let {
            messagesListener = chatRepo.listenMessages(it) { newMessages ->
                _messages.postValue(newMessages)
            }
        }
    }

    fun sendMessage(text: String) {
        if (chatId != null && senderId != null) {
            chatRepo.sendMessage(chatId!!, senderId!!, text)
        }
    }

    fun stopListening() {
        messagesListener?.remove()
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}
