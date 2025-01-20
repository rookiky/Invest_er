package com.example.invest

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

@Composable
fun FounderProfileScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Founder Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = "My Startup",
            onValueChange = {},
            label = { Text("Company Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = "Innovative AI platform",
            onValueChange = {},
            label = { Text("Pitch") }
        )
    }
}

fun saveFounderProfile(userId: String, name: String, projectDescription: String, profileImage: String) {
    val database = FirebaseDatabase.getInstance()
    val founderRef = database.getReference("Users/Founders/$userId")

    val founderData = mapOf(
        "name" to name,
        "projectDescription" to projectDescription,
        "profileImage" to profileImage,
        "createdAt" to System.currentTimeMillis().toString() // Timestamp for sorting
    )

    founderRef.setValue(founderData)
        .addOnSuccessListener {
            Log.d("SaveProfile", "Profile saved successfully!")
        }
        .addOnFailureListener { e ->
            Log.e("SaveProfile", "Failed to save profile", e)
        }
}

