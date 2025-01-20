package com.example.invest.viewModel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
