package com.example.invest.favoriteScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invest.data.Project
import com.example.invest.viewModel.FavoriteViewModel

@Composable
fun FavoritesScreen(viewModel: FavoriteViewModel = viewModel()) {
    val favorites = viewModel.favorites.collectAsState().value

    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text("No favorite projects available.")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favorites) { project ->
                FavoriteProjectCard(project = project,
                    onLike = { viewModel.likeProject(it) },
                    onDislike = { viewModel.dislikeProject(it) })
            }
        }
    }
}

@Composable
fun FavoriteProjectCard(
    project: Project,
    onLike: (Project) -> Unit,
    onDislike: (Project) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = project.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Created at: ${project.createdAt}",
                style = MaterialTheme.typography.bodySmall
            )
            Row(modifier = Modifier.padding(start = 8.dp)) {
                Button(
                    onClick = { onDislike(project) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Dislike")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onLike(project) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Like")
                }
            }
        }
    }
}
