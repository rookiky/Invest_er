package com.example.invest.messageScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invest.viewModel.ChatRoomViewModel
import com.example.invest.viewModel.ChatRoomViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatRoomScreen(chatId: String) {
    val factory = remember { ChatRoomViewModelFactory(chatId) }
    val viewModel: ChatRoomViewModel = viewModel(factory = factory)

    val messages = viewModel.messages.collectAsState().value
    var messageContent by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chat ID: $chatId",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                for (message in messages) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
        Row(modifier = Modifier.padding(top = 8.dp)) {
            TextField(
                value = messageContent,
                onValueChange = { messageContent = it },
                placeholder = { Text("Type a message") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (messageContent.isNotBlank()) {
                    viewModel.sendMessage(messageContent)
                    messageContent = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}
