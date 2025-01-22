package com.example.invest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invest.data.InvestorProfile
import com.example.invest.viewModel.InvestorProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestorProfileScreen(viewModel: InvestorProfileViewModel = viewModel()) {
    val profile by viewModel.profile.collectAsState()

    var name by remember { mutableStateOf(profile.name) }
    var description by remember { mutableStateOf(profile.description) }
    var investmentBudget by remember { mutableStateOf(profile.investmentBudget) }
    var investmentHorizon by remember { mutableStateOf(profile.investmentHorizon) }
    var startupStageInterest by remember { mutableStateOf(profile.startupStageInterest) }
    var investedCompanies by remember { mutableStateOf(profile.investedCompanies.joinToString(", ")) }

    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        focusedTextColor = MaterialTheme.colorScheme.onBackground
    )

    LaunchedEffect(profile) {
        name = profile.name
        description = profile.description
        investmentBudget = profile.investmentBudget
        investmentHorizon = profile.investmentHorizon
        startupStageInterest = profile.startupStageInterest
        investedCompanies = profile.investedCompanies.joinToString(", ")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Investor Profile", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors
        )

        OutlinedTextField(
            value = investmentBudget,
            onValueChange = { investmentBudget = it },
            label = { Text("Investment Budget") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = investmentHorizon,
            onValueChange = { investmentHorizon = it },
            label = { Text("Investment Horizon (Short, Medium, Long)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = startupStageInterest,
            onValueChange = { startupStageInterest = it },
            label = { Text("Startup Stage Interest (Seed, Growth, Late)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = investedCompanies,
            onValueChange = { investedCompanies = it },
            label = { Text("Invested Companies (comma-separated)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        Button(
            onClick = {
                val updatedProfile = profile.copy(
                    name = name,
                    description = description,
                    investmentBudget = investmentBudget,
                    investmentHorizon = investmentHorizon,
                    startupStageInterest = startupStageInterest,
                    investedCompanies = investedCompanies.split(", ").map { it.trim() }
                )
                viewModel.updateProfile(updatedProfile) { success ->
                    if (success) {
                        println("Profile updated successfully")
                    } else {
                        println("Failed to update profile")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )

        ) {
            Text("Save Changes")
        }
    }
}
