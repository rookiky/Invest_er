package com.example.invest.messageScreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.example.invest.viewModel.MessagesViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.invest.data.ChatRoom
import com.example.invest.data.Message
import com.example.invest.viewModel.MessageViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun MessagesScreen(viewModel: MessagesViewModel = viewModel(), navController: NavHostController) {
    val chatRooms by viewModel.chatRooms.collectAsState()

    if (chatRooms.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No active chats")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatRooms) { chatRoom ->
                ChatRoomCard(
                    chatRoom = chatRoom,
                    onClick = { navController.navigate("messages/${chatRoom.chatId}") }
                )
            }
        }
    }
}

@Composable
fun ChatRoomCard(chatRoom: ChatRoom, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(chatRoom.otherUserName, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(chatRoom.lastMessage, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
