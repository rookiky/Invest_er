package com.example.invest

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun FounderProfileScreen(viewModel: FounderProfileViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var projectDescription by remember { mutableStateOf("") }
    var profileImage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = projectDescription,
            onValueChange = { projectDescription = it },
            label = { Text("Project Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = profileImage,
            onValueChange = { profileImage = it },
            label = { Text("Profile Image URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.saveFounderProfile(name, projectDescription, profileImage)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }
    }
}

class FounderProfileViewModel : ViewModel() {
    fun saveFounderProfile(name: String, projectDescription: String, profileImage: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val founderRef = database.getReference("Users/Founders/$userId")

        val founderData = mapOf(
            "id" to userId,
            "name" to name,
            "projectDescription" to projectDescription,
            "profileImage" to profileImage,
            "createdAt" to System.currentTimeMillis()
        )

        founderRef.setValue(founderData)
            .addOnSuccessListener {
                println("Profile saved successfully")
            }
            .addOnFailureListener {
                println("Failed to save profile: ${it.message}")
            }
    }
}
