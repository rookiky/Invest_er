package com.example.invest.homeScreen

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invest.data.InvestorProfile
import com.example.invest.viewModel.FounderHomeViewModel

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
                    println("current investor: $currentInvestor")
                    println("currentInvestorId: ${currentInvestor.id}")
                    println("Chat room created with ID: $chatRoomId")
                }
                currentIndex++
            }
        )
    }
}

@Composable
fun SwipeableInvestorCard(
    investor: InvestorProfile,
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
        InvestorCard(investor = investor)
    }
}

@Composable
fun InvestorCard(investor: InvestorProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Name: ${investor.name}", style = MaterialTheme.typography.headlineSmall)
            Text("Interests: ${investor.description}", style = MaterialTheme.typography.bodyMedium)
            Text("Budget: ${investor.investmentBudget}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
