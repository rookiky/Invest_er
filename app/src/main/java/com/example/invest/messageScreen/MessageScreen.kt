package com.example.invest.messageScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.invest.data.Message
import com.example.invest.viewModel.ChatViewModel
import com.example.invest.viewModel.MessageViewModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MessageScreen(chatId: String, viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.getMessages(chatId).collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true // Messages stack from bottom
        ) {
            items(messages) { message ->
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var text by remember { mutableStateOf("") }
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }
            )
            Button(onClick = {
                viewModel.sendMessage(chatId, text)
                text = ""
            }) {
                Text("Send")
            }
        }
    }
}


@Composable
fun MessageBubble(message: Message) {
    Column(
        horizontalAlignment = if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(
            text = message.content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
        Text(
            text = "Sent at: ${message.timestamp}",
            style = MaterialTheme.typography.labelSmall
        )
    }
}
