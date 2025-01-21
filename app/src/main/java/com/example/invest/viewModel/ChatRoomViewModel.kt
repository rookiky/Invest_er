package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.invest.data.Message
import com.google.firebase.auth.FirebaseAuth
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

        messagesRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                _messages.value = messages.sortedBy { it.timestamp }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                println("Failed to fetch messages: ${error.message}")
            }
        })
    }

    fun sendMessage(content: String) {
        val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.getReference("Chats/$chatId/messages").push()

        val message = Message(
            messageId = messagesRef.key ?: "",
            senderId = senderId,
            receiverId = "",
            content = content,
            timestamp = System.currentTimeMillis()
        )

        messagesRef.setValue(message).addOnSuccessListener {
            println("Message sent successfully")
            updateLastMessage(content)
        }.addOnFailureListener {
            println("Failed to send message: ${it.message}")
        }
    }

    private fun updateLastMessage(content: String) {
        val database = FirebaseDatabase.getInstance()
        val chatRef = database.getReference("Chats/$chatId")

        chatRef.child("lastMessage").setValue(content).addOnSuccessListener {
            println("Last message updated successfully")
        }.addOnFailureListener {
            println("Failed to update last message: ${it.message}")
        }
    }
}

class ChatRoomViewModelFactory(private val chatId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatRoomViewModel(chatId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
