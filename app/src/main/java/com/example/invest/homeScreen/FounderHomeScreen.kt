package com.example.invest.homeScreen

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invest.data.InvestorProfile
import com.example.invest.viewModel.FounderHomeViewModel
import kotlinx.coroutines.launch

@Composable
fun FounderHomeScreen(viewModel: FounderHomeViewModel = viewModel()) {
    val investors = viewModel.investors.collectAsState().value
    var currentIndex by remember { mutableStateOf(0) }

    if (investors.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No investors have liked your projects yet.")
        }
    } else if (currentIndex >= investors.size) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No more investors to review.")
        }
    } else {
        val currentInvestor = investors[currentIndex]

        SwipeableInvestorCard(
            investor = currentInvestor,
            onSwipeLeft = {
                viewModel.rejectInvestor(currentInvestor.id)
                currentIndex++
            },
            onSwipeRight = {
                viewModel.likeInvestor(currentInvestor.id) { chatRoomId ->
                    println("Chat room created with ID: $chatRoomId")
                }
                currentIndex++
            },
            onLike = {
                    investorId -> viewModel.likeInvestor(currentInvestor.id)  { chatRoomId ->
                println("Chat room created with ID: $chatRoomId")
            }
                currentIndex++ },
            onDislike = {
                    investorId -> viewModel.rejectInvestor(currentInvestor.id)
                currentIndex++
            }

        )
    }
}

@Composable
fun SwipeableInvestorCard(
    investor: InvestorProfile,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onLike: (String) -> Unit,
    onDislike: (String) -> Unit
) {
    val offsetX = remember { Animatable(0f) } // For swipe animation
    val swipeThreshold = 300f // Threshold for swipe actions
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize() // Ensures the swipeable area takes the entire screen
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
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
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount) // Update offset immediately
                        }
                    }
                )
            }
            .offset { IntOffset(offsetX.value.toInt(), 0) }, // Move card horizontally
        contentAlignment = Alignment.Center // Center the card
    ) {
        InvestorCard(
            investor = investor,
            onLike = { onLike(investor.id) },
            onDislike = { onDislike(investor.id) }
        )
    }

}

@Composable
fun InvestorCard(
    investor: InvestorProfile,
    onLike: (String) -> Unit,
    onDislike: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,

        ) {
            Text("Name: ${investor.name}", style = MaterialTheme.typography.headlineSmall)
            Text("Interests: ${investor.description}", style = MaterialTheme.typography.bodyMedium)
            Text("Budget: ${investor.investmentBudget}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircularIconButton(
                    icon = Icons.Default.Close,
                    description = "Dislike",
                    onClick = {onDislike(investor.id)},
                    buttonColor = Color.White,
                    iconColor = Color(0xFFFF5252),
                )
                CircularIconButton(
                    icon = Icons.Default.Favorite,
                    description = "Like",
                    onClick = { onLike(investor.id) },
                    buttonColor = Color.White,
                    iconColor = Color(0xFF4CAF50),
                )
            }
        }

    }
}
