package com.example.invest.data

import com.google.firebase.Timestamp

data class ChatRoom(
    val chatId: String = "",
    val user1Id: String = "",
    val user2Id: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L
)