package com.example.invest.homeScreen

import android.widget.Toast
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invest.data.Project
import com.example.invest.viewModel.InvestorHomeViewModel

@Composable
fun InvestorHomeScreen(viewModel: InvestorHomeViewModel = viewModel()) {
    val projects = viewModel.projects.collectAsState().value
    var currentIndex by remember { mutableIntStateOf(0) }

    if (projects.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No projects available", style = MaterialTheme.typography.bodyLarge)
        }
    } else if (currentIndex >= projects.size) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No more projects to review", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        val currentProject = projects[currentIndex]

        SwipeableBox(
            project = currentProject,
            onSwipeLeft = {
                viewModel.dislikeProject(currentProject.id)
                currentIndex++
            },
            onSwipeRight = {
                viewModel.likeProject(currentProject.id)
                currentIndex++
            },
            onFavorite = {
                projectId -> viewModel.addFavoriteProject(projectId)
            },
            onLike = {
                projectId -> viewModel.likeProject(projectId)
                currentIndex++ },
            onDislike = {
                projectId -> viewModel.dislikeProject(projectId)
                currentIndex++
            }
        )
    }
}

@Composable
fun ProjectCard(
    project: Project,
    onFavorite: (String) -> Unit,
    onLike: (String) -> Unit,
    onDislike: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(project.name, style = MaterialTheme.typography.headlineSmall)
            Text(project.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { onDislike(project.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Dislike")
                }
                Button(
                    onClick = { onFavorite(project.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
                ) {
                    Text("Favorite")
                }
                Button(
                    onClick = { onLike(project.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Like")
                }
            }
        }
    }
}

@Composable
fun SwipeableBox(
    project: Project,
    onLike: (String) -> Unit,
    onDislike: (String) -> Unit,
    onFavorite: (String) -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    when {
                        dragAmount < -100 -> onSwipeLeft()
                        dragAmount > 100 -> onSwipeRight()
                    }
                }
            }
    ) {
        ProjectCard(
            project,
            onLike = {onLike(project.id)},
            onDislike = {onDislike(project.id)},
            onFavorite = {onFavorite(project.id)}
        )
    }
}

