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
import androidx.navigation.NavHostController
import com.example.invest.data.ChatRoom
import com.example.invest.viewModel.MessagesViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MessagesScreen(viewModel: MessagesViewModel = viewModel(), navController: NavHostController) {
    val chatRooms = viewModel.chatRooms.collectAsState().value
    val userNames = viewModel.userNames.collectAsState().value

    if (chatRooms.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No active chats")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatRooms) { chatRoom ->
                val userName = userNames[chatRoom.user2Id] ?: "Unknown User"
                ChatRoomCard(
                    chatRoom = chatRoom,
                    userName = userName,
                    onClick = {
                        println("chatRoom Id Card: ${chatRoom.chatId}")
                        navController.navigate("Chats/${chatRoom.chatId}")
                    }
                )
            }
        }
    }
}

@Composable
fun ChatRoomCard(chatRoom: ChatRoom, userName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(userName, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(chatRoom.lastMessage, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
