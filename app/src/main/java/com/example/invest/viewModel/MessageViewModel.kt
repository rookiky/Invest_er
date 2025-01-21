package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import com.example.invest.data.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot

class MessageViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun sendMessage(chatId: String, content: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (userId == null) {
            onFailure("User not authenticated")
            return
        }

        val messageId = database.getReference("Chats/$chatId/messages").push().key ?: return
        val message = Message(senderId = userId, content = content, timestamp = System.currentTimeMillis())

        database.getReference("Chats/$chatId/messages/$messageId").setValue(message)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Failed to send message") }
    }

    fun fetchMessages(chatId: String, onMessagesFetched: (List<Message>) -> Unit) {
        database.getReference("Chats/$chatId/messages").get()
            .addOnSuccessListener { snapshot ->
                val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                onMessagesFetched(messages)
            }
            .addOnFailureListener {
                println("Failed to fetch messages: ${it.message}")
            }
    }
}
