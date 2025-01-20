package com.example.invest

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InvestorProfileScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Investor Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = "Tech Startups",
            onValueChange = {},
            label = { Text("Investment Interest") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = "50k-500k",
            onValueChange = {},
            label = { Text("Budget Range") }
        )
    }
}
