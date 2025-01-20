package com.example.invest.homeScreen

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.invest.data.InvestorProfile
import com.example.invest.viewModel.FounderHomeViewModel

@Composable
fun FounderHomeScreen(viewModel: FounderHomeViewModel = viewModel()) {
    val investors = viewModel.investors.collectAsState().value
    var currentIndex by remember { mutableIntStateOf(0) }

    if (investors.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No projects available", style = MaterialTheme.typography.bodyLarge)
        }
    } else if (currentIndex >= investors.size) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No more projects to review", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        val currentInvestor = investors[currentIndex]

        SwipeableBox(
            investor = currentInvestor,
            onSwipeLeft = {
                viewModel.dislikeInvestor(currentInvestor.id)
                currentIndex++
            },
            onSwipeRight = {
                viewModel.likeInvestor(currentInvestor.id)
                currentIndex++
            },
            onLike = {
                    investorId -> viewModel.likeInvestor(investorId)
            },
            onDislike = {
                    investorId -> viewModel.dislikeInvestor(investorId)
            }
        )
    }
}

@Composable
fun InvestorCard(
    investor: InvestorProfile,
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
            Text(investor.name, style = MaterialTheme.typography.headlineSmall)
            Text(investor.description, style = MaterialTheme.typography.bodyMedium)
            Text(investor.investmentBudget,style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { onDislike(investor.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Dislike")
                }
                Button(
                    onClick = { onLike(investor.id) },
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
    investor: InvestorProfile,
    onLike: (String) -> Unit,
    onDislike: (String) -> Unit,
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
        InvestorCard(
            investor,
            onLike = {onLike(investor.id)},
            onDislike = {onDislike(investor.id)}
        )
    }
}



