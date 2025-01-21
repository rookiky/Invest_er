package com.example.invest.viewModel
import androidx.lifecycle.ViewModel
import com.example.invest.data.ChatRoom
import com.example.invest.data.FounderProfile
import com.example.invest.data.Project
import com.example.invest.utils.fetchFounders
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val userChatsRef = database.getReference("Users/$userId/chats")

        userChatsRef.get()
            .addOnSuccessListener { snapshot ->
                val rooms = snapshot.children.mapNotNull { it.getValue(ChatRoom::class.java) }
                _chatRooms.value = rooms
                println("Succeeded to fetch chat rooms")
            }
            .addOnFailureListener {
                println("Failed to fetch chat rooms: ${it.message}")
            }
    }
}
