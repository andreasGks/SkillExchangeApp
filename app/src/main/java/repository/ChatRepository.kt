package repository

import com.example.skillexchangeapp.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createChat(participants: List<String>, onComplete: (String) -> Unit) {
        val chatData = hashMapOf(
            "participants" to participants,
            "lastMessage" to "",
            "lastTimestamp" to System.currentTimeMillis()
        )
        db.collection("chats")
            .add(chatData)
            .addOnSuccessListener { doc -> onComplete(doc.id) }
            .addOnFailureListener { /* handle error */ }
    }

    fun sendMessage(chatId: String, senderId: String, text: String) {
        val messageData = hashMapOf(
            "senderId" to senderId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(messageData)

        db.collection("chats")
            .document(chatId)
            .update(
                "lastMessage", text,
                "lastTimestamp", System.currentTimeMillis()
            )
    }

    fun listenMessages(chatId: String, onMessages: (List<Message>) -> Unit): ListenerRegistration {
        return db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val messages = snapshot.documents.map {
                    Message(
                        id = it.id,
                        senderId = it.getString("senderId") ?: "",
                        text = it.getString("text") ?: "",
                        timestamp = it.getLong("timestamp") ?: 0L
                    )
                }
                onMessages(messages)
            }
    }
}
