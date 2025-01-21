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
import com.example.invest.viewModel.FounderProfileViewModel

@Composable
fun FounderProfileScreen(
    founderId: String,
    viewModel: FounderProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val projects = remember { mutableStateListOf<Project>() }

    var name by remember { mutableStateOf("") }
    var projectDescription by remember { mutableStateOf("") }
    var profileImage by remember { mutableStateOf("") }

    // Fetch founder's projects
    LaunchedEffect(founderId) {
        viewModel.fetchProjectsForFounder(
            userId = founderId,
            onResult = { fetchedProjects ->
                projects.clear()
                projects.addAll(fetchedProjects)
            },
            onFailure = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Edit Profile", style = MaterialTheme.typography.headlineMedium)
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = projectDescription,
            onValueChange = { projectDescription = it },
            label = { Text("Project Description") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = profileImage,
            onValueChange = { profileImage = it },
            label = { Text("Profile Image URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                viewModel.saveFounderProfile(
                    context = context,
                    name = name,
                    projectDescription = projectDescription,
                    profileImage = profileImage
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }
    }
}
