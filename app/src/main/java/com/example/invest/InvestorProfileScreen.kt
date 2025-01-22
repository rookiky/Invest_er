package com.example.invest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.invest.viewModel.InvestorProfileViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestorProfileScreen(
    viewModel: InvestorProfileViewModel = viewModel(),
    navController: NavHostController
) {
    val profile by viewModel.profile.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var investmentBudget by remember { mutableStateOf("") }
    var investmentHorizon by remember { mutableStateOf("") }
    var startupStageInterest by remember { mutableStateOf("") }
    var investedCompanies by remember { mutableStateOf("") }

    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        focusedTextColor = MaterialTheme.colorScheme.onBackground
    )

    // Load profile values into fields when profile changes
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

        // Name Field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        // Description Field
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        // Investment Budget Field
        OutlinedTextField(
            value = investmentBudget,
            onValueChange = { investmentBudget = it },
            label = { Text("Investment Budget") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        // Investment Horizon Field
        OutlinedTextField(
            value = investmentHorizon,
            onValueChange = { investmentHorizon = it },
            label = { Text("Investment Horizon (Short, Medium, Long)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        // Startup Stage Interest Field
        OutlinedTextField(
            value = startupStageInterest,
            onValueChange = { startupStageInterest = it },
            label = { Text("Startup Stage Interest (Seed, Growth, Late)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        // Invested Companies Field
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

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true } // Clears back stack
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Disconnect")
        }
    }
}
