package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import com.example.invest.data.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MessagesViewModel : ViewModel() {
    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> get() = _chatRooms

    init {
        fetchChatRooms()
    }

    private fun fetchChatRooms() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val chatRoomsRef = database.getReference("Users/$userId/chatRooms")

        chatRoomsRef.get()
            .addOnSuccessListener { snapshot ->
                val chatRooms = snapshot.children.mapNotNull { it.getValue(ChatRoom::class.java) }
                _chatRooms.value = chatRooms.sortedByDescending { it.timestamp }
            }
            .addOnFailureListener {
                println("Failed to fetch chat rooms: ${it.message}")
            }
    }
}
