package com.example.invest.messageScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invest.data.ChatRoom
import com.example.invest.viewModel.MessagesViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MessagesScreen(viewModel: MessagesViewModel = viewModel(), navController: NavHostController) {
    val chatRooms = viewModel.chatRooms.collectAsState().value

    if (chatRooms.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No active chats", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatRooms) { chatRoom ->
                val chatId = chatRoom.chatId
                ChatRoomCard(
                    chatRoom = chatRoom,
                    onClick = { navController.navigate("Chats/$chatId") }
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
            Text(
                text = "Chat with ${chatRoom.user1Id.takeIf { it != FirebaseAuth.getInstance().currentUser?.uid } ?: chatRoom.user2Id}",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(chatRoom.lastMessage, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
