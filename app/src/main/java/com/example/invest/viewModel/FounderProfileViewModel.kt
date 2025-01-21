package com.example.invest.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.invest.data.FounderProfile
import com.example.invest.data.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FounderProfileViewModel : ViewModel() {

    fun saveFounderProfile(context: Context, name: String, projectDescription: String, profileImage: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val founderRef = database.getReference("Users/Founders/$userId")
        Log.d("userId:", userId)
        Log.d("FounderRef", founderRef.toString())

        val founderData = FounderProfile(
            id = userId,
            name = name,
            projectDescription = projectDescription,
            profileImage = profileImage,
            createdAt = System.currentTimeMillis()
        )


        founderRef.setValue(founderData)
            .addOnSuccessListener {
                Toast.makeText(context, "Profile saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(context, "Failed to save profile: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun fetchProjectsForFounder(
        userId: String,
        onResult: (List<Project>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance()
        val founderProjectsRef = database.getReference("Users/$userId/projects")

        founderProjectsRef.get()
            .addOnSuccessListener { snapshot ->
                val projectIds = snapshot.children.mapNotNull { it.key }
                val projects = mutableListOf<Project>()

                for (projectId in projectIds) {
                    val projectRef = database.getReference("Projects/$projectId")
                    projectRef.get()
                        .addOnSuccessListener { projectSnapshot ->
                            val project = projectSnapshot.getValue(Project::class.java)
                            if (project != null) projects.add(project)

                            // Return results when all projects are loaded
                            if (projects.size == projectIds.size) {
                                onResult(projects)
                            }
                        }
                        .addOnFailureListener { onFailure(it.message ?: "Failed to fetch project details") }
                }
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to fetch founder projects") }
    }



    fun saveProject(
        projectName: String,
        projectDescription: String,
        founderId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance()
        val projectRef = database.getReference("Projects").push()
        val projectId = projectRef.key ?: run {
            onFailure("Failed to generate project ID")
            return
        }

        val founderId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            onFailure("User is not authenticated")
            return
        }

        val project = Project(
            id = projectId,
            name = projectName,
            description = projectDescription,
            founderId = founderId,
            createdAt = System.currentTimeMillis()
        )

        projectRef.setValue(project)
            .addOnSuccessListener {

                val founderRef = database.getReference("Users/$founderId/projects/$projectId")
                founderRef.setValue(true)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { error ->
                        onFailure("Failed to associate project with founder: ${error.message}")
                    }
            }
            .addOnFailureListener { error ->
                onFailure("Failed to save project: ${error.message}")
            }
    }

}
