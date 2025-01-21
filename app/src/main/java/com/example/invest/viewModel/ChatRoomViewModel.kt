package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import com.example.invest.data.Message
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatRoomViewModel(private val chatId: String) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> get() = _messages

    init {
        fetchMessages()
    }

    private fun fetchMessages() {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("Chats/$chatId/messages")

        messagesRef.get()
            .addOnSuccessListener { snapshot ->
                val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                _messages.value = messages.sortedBy { it.timestamp }
            }
            .addOnFailureListener {
                println("Failed to fetch messages: ${it.message}")
            }
    }

    fun sendMessage(senderId: String, content: String) {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("Chats/$chatId/messages").push()

        val message = Message(
            messageId = messagesRef.key ?: "",
            senderId = senderId,
            receiverId = "", // Fetch dynamically
            content = content,
            timestamp = System.currentTimeMillis()
        )

        messagesRef.setValue(message).addOnSuccessListener {
            println("Message sent successfully")
        }.addOnFailureListener {
            println("Failed to send message: ${it.message}")
        }
    }
}
