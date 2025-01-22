package com.example.invest

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invest.data.Project
import com.example.invest.utils.NotificationHandler
import com.example.invest.viewModel.FounderProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FounderProfileScreen(
    founderId: String,
    viewModel: FounderProfileViewModel = viewModel()
) {

    NotificationHandler(userId = founderId)
    val context = LocalContext.current
    val projects = remember { mutableStateListOf<Project>() }

    val profile by viewModel.profile.collectAsState()

    var name by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var industryFocus by remember { mutableStateOf("") }
    var linkedinProfile by remember { mutableStateOf("") }
    var profileImage by remember { mutableStateOf("") }

    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        focusedTextColor = MaterialTheme.colorScheme.onBackground
    )

    LaunchedEffect(founderId) {
        viewModel.fetchFounderProfile(founderId) { profile ->
            name = profile.name
            experience = profile.experience
            industryFocus = profile.industryFocus
            linkedinProfile = profile.linkedinProfile
            profileImage = profile.profileImage
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Edit Profile", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = experience,
            onValueChange = { experience = it },
            label = { Text("Experience (years)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = industryFocus,
            onValueChange = { industryFocus = it },
            label = { Text("Industry Focus") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = linkedinProfile,
            onValueChange = { linkedinProfile = it },
            label = { Text("LinkedIn Profile URL") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = profileImage,
            onValueChange = { profileImage = it },
            label = { Text("Profile Image URL") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        Button(
            onClick = {
                val updatedProfile = profile.copy(
                    name = name,
                    experience = experience,
                    industryFocus = industryFocus,
                    linkedinProfile = linkedinProfile,
                    profileType = "Founder"

                )
                viewModel.updateProfile(updatedProfile) { success ->
                    if (success) {
                        Toast.makeText(context, "Project saved successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        println("Failed to update profile")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }
    }
}