package com.example.invest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun InvestorHomeScreen(viewModel: InvestorHomeViewModel = viewModel()) {
    val founders by viewModel.founders.collectAsState()

    if (founders.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No profiles available", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(founders) { founder ->
                FounderCard(
                    founder = FounderProfile(),
                    onLike = { viewModel.likeFounder(founder.id) },
                    onDislike = { viewModel.dislikeFounder(founder.id) }
                )
            }
        }
    }
}

@Composable
fun FounderCard(
    founder: FounderProfile,
    onLike: (String) -> Unit,
    onDislike: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(founder.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(founder.projectDescription, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = { onDislike(founder.id) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Dislike")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onLike(founder.id) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Text("Like")
                }
            }
        }
    }
}
