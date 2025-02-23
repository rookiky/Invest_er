package com.example.invest.homeScreen

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invest.data.Project
import com.example.invest.viewModel.InvestorHomeViewModel
import kotlinx.coroutines.launch

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
                currentIndex++
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
fun SwipeableBox(
    project: Project,
    onLike: (String) -> Unit,
    onDislike: (String) -> Unit,
    onFavorite: (String) -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val offsetX = remember { Animatable(0f) } //
    val swipeThreshold = 300f
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount)
                        }
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            when {
                                offsetX.value < -swipeThreshold -> {
                                    onSwipeLeft()
                                    offsetX.snapTo(0f)
                                }
                                offsetX.value > swipeThreshold -> {
                                    onSwipeRight()
                                    offsetX.snapTo(0f)
                                }
                                else -> {
                                    offsetX.animateTo(0f)
                                }
                            }
                        }
                    }
                )
            }
            .offset { IntOffset(offsetX.value.toInt(), 0) },
        contentAlignment = Alignment.Center
    ) {
        ProjectCard(
            project = project,
            onLike = { onLike(project.id) },
            onDislike = { onDislike(project.id) },
            onFavorite = { onFavorite(project.id) }
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
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween // Ensures spacing between top content and bottom buttons
        ) {
            // Top Content: Project Title and Details
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Project Title
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Project Details
                DetailRow("Description:", project.description)
                DetailRow("Phase:", project.developmentPhase)
                DetailRow("Min Investment:", project.minimumInvestment)
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircularIconButton(
                    icon = Icons.Default.Close,
                    description = "Dislike",
                    onClick = { onDislike(project.id) },
                    buttonColor = Color.White,
                    iconColor = Color(0xFFFF5252) // Red for Dislike
                )
                CircularIconButton(
                    icon = Icons.Default.Star,
                    description = "Favorite",
                    onClick = { onFavorite(project.id) },
                    buttonColor = Color.White,
                    iconColor = Color(0xFF03A9F4) // Blue for Favorite
                )
                CircularIconButton(
                    icon = Icons.Default.Favorite,
                    description = "Like",
                    onClick = { onLike(project.id) },
                    buttonColor = Color.White,
                    iconColor = Color(0xFF4CAF50) // Green for Like
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}



@Composable
fun CircularIconButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    buttonColor: Color,
    iconColor: Color,
    buttonSize: Dp = 56.dp,
    iconSize: Dp = 32.dp
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(50)), // Circular shape
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor), // Button background color
        contentPadding = PaddingValues(0.dp), // Ensure proper centering
        modifier = Modifier.size(buttonSize) // Size of the button
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}


