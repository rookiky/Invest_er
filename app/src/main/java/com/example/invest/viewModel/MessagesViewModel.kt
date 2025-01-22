package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import com.example.invest.data.ChatRoom
import com.example.invest.data.InvestorProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MessagesViewModel : ViewModel() {
    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> get() = _chatRooms
    private val _userNames = MutableStateFlow<Map<String, String>>(emptyMap())

    val userNames: StateFlow<Map<String, String>> get() = _userNames
    private val database = FirebaseDatabase.getInstance()

    init {
        fetchChatRooms()
    }

    private fun fetchChatRooms() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val chatRoomsRef = database.getReference("Users/$userId/chatRooms")

        chatRoomsRef.get().addOnSuccessListener { snapshot ->
            val chatRooms = snapshot.children.mapNotNull { it.getValue(ChatRoom::class.java) }
            _chatRooms.value = chatRooms


            val user2Ids = chatRooms.map { it.user2Id }.distinct()
            fetchUserNames(user2Ids)
        }.addOnFailureListener {
            println("Failed to fetch chat rooms: ${it.message}")
        }
    }

    private fun fetchUserNames(userIds: List<String>) {
        val userNamesMap = mutableMapOf<String, String>()

        userIds.forEach { userId ->
            val userRef = database.getReference("Users/$userId")

            userRef.get().addOnSuccessListener { snapshot ->
                val name = snapshot.child("name").getValue(String::class.java)
                if (name != null) {
                    userNamesMap[userId] = name
                    _userNames.value = userNamesMap
                }
            }.addOnFailureListener {
                println("Failed to fetch user name for $userId: ${it.message}")
            }
        }
    }
}
