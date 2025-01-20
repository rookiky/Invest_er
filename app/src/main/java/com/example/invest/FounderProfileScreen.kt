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

        Divider(thickness = 2.dp)

        Text("My Projects", style = MaterialTheme.typography.headlineMedium)

        if (projects.isEmpty()) {
            Text("No projects found. Add a new project to get started.")
        } else {
            projects.forEach { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(project.name, style = MaterialTheme.typography.titleLarge)
                        Text(project.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section for adding new projects
        var newProjectName by remember { mutableStateOf("") }
        var newProjectDescription by remember { mutableStateOf("") }

        TextField(
            value = newProjectName,
            onValueChange = { newProjectName = it },
            label = { Text("New Project Name") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = newProjectDescription,
            onValueChange = { newProjectDescription = it },
            label = { Text("New Project Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                viewModel.saveProject(
                    projectName = newProjectName,
                    projectDescription = newProjectDescription,
                    founderId = founderId,
                    onSuccess = {
                        Toast.makeText(context, "Project saved successfully", Toast.LENGTH_SHORT).show()
                        newProjectName = ""
                        newProjectDescription = ""
                        // Refresh project list
                        viewModel.fetchProjectsForFounder(
                            userId = founderId,
                            onResult = { refreshedProjects ->
                                projects.clear()
                                projects.addAll(refreshedProjects)
                            },
                            onFailure = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onFailure = { message ->
                        Toast.makeText(context, "Failed to save project: $message", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Project")
        }
    }
}
