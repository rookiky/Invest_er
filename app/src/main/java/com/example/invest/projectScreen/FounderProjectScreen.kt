package com.example.invest.projectScreen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.invest.data.Project
import com.example.invest.viewModel.FounderProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FounderProjectScreen(
    founderId: String,
    viewModel: FounderProjectViewModel = viewModel()
) {
    val projects = remember { mutableStateListOf<Project>() }
    val context = LocalContext.current

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

    var projectName by remember { mutableStateOf("") }
    var projectDescription by remember { mutableStateOf("") }
    var projectField by remember { mutableStateOf("") }
    var minimumInvestment by remember { mutableStateOf("") }
    var developmentPhase by remember { mutableStateOf("") }
    var partners by remember { mutableStateOf("") }

    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        focusedTextColor = MaterialTheme.colorScheme.onBackground
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("My Projects", style = MaterialTheme.typography.headlineMedium)

        // Display existing projects
        if (projects.isEmpty()) {
            Text("No projects found. Add a new project to get started.")
        } else {
            projects.forEach { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(project.name, style = MaterialTheme.typography.titleLarge)
                        Text("Field: ${project.field}", style = MaterialTheme.typography.bodyMedium)
                        Text("Minimum Investment: ${project.minimumInvestment}", style = MaterialTheme.typography.bodyMedium)
                        Text("Development Phase: ${project.developmentPhase}", style = MaterialTheme.typography.bodyMedium)
                        Text("Partners: ${project.partners.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = projectName,
            onValueChange = { projectName = it },
            label = { Text("Project Name") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = projectField,
            onValueChange = { projectField = it },
            label = { Text("Field") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = minimumInvestment,
            onValueChange = { minimumInvestment = it },
            label = { Text("Minimum Investment Required") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = developmentPhase,
            onValueChange = { developmentPhase = it },
            label = { Text("Development Phase (Seed, Growth, Late)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        OutlinedTextField(
            value = partners,
            onValueChange = { partners = it },
            label = { Text("Partners (comma-separated)") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors
        )

        // Save Project Button
        Button(
            onClick = {
                if (projectName.isNotBlank()) {
                    viewModel.saveProject(
                        projectName = projectName,
                        projectDescription = projectDescription,
                        projectField = projectField,
                        minimumInvestment = minimumInvestment,
                        developmentPhase = developmentPhase,
                        partners = partners.split(", ").map { it.trim() },
                        founderId = founderId,
                        onSuccess = {
                            Toast.makeText(context, "Project saved successfully", Toast.LENGTH_SHORT).show()
                            projectName = ""
                            projectField = ""
                            minimumInvestment = ""
                            developmentPhase = ""
                            partners = ""
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
                } else {
                    Toast.makeText(context, "Project Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Add Project")
        }
    }
}
