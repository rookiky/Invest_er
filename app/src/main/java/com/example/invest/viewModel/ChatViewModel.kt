package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import com.example.invest.data.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages

    private val userId: String = FirebaseAuth.getInstance().currentUser?.uid
        ?: throw IllegalStateException("User not authenticated")

    /**
     * Fetch messages for a specific chatId.
     */
    fun getMessages(chatId: String): StateFlow<List<Message>> {
        val database = FirebaseDatabase.getInstance()
        val chatMessagesRef = database.getReference("Chats/$chatId/messages")

        chatMessagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fetchedMessages = snapshot.children.mapNotNull {
                    it.getValue(Message::class.java)
                }.sortedBy { it.timestamp } // Sort messages by timestamp
                _messages.value = fetchedMessages
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to fetch messages: ${error.message}")
            }
        })

        return messages
    }

    /**
     * Send a new message to the chat.
     */
    fun sendMessage(chatId: String, content: String) {
        if (content.isBlank()) return

        val database = FirebaseDatabase.getInstance()
        val chatMessagesRef = database.getReference("Chats/$chatId/messages").push()

        val message = Message(
            senderId = userId,
            content = content,
            timestamp = System.currentTimeMillis()
        )

        chatMessagesRef.setValue(message)
            .addOnSuccessListener {
                println("Message sent successfully!")
            }
            .addOnFailureListener {
                println("Failed to send message: ${it.message}")
            }

        // Update the last message in the chat room for quick access
        val chatRef = database.getReference("Chats/$chatId/lastMessage")
        chatRef.setValue(content)
    }
}
