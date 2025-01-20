package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import com.example.invest.data.FounderProfile
import com.example.invest.data.Project
import com.example.invest.utils.fetchFounders
import com.example.invest.utils.fetchProjects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InvestorHomeViewModel : ViewModel() {
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> get() = _projects

    init {
        fetchProjects(
            onResult = { fetchedProjects ->
                _projects.value = fetchedProjects
            },
            onFailure = { errorMessage ->
                println("Error fetching projects: $errorMessage")
            }
        )
    }

    fun fetchProjects(
        onResult: (List<Project>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance()
        val founderProjectsRef = database.getReference("Projects")

        founderProjectsRef.get()
            .addOnSuccessListener { snapshot ->
                val projects = snapshot.children.mapNotNull { it.getValue(Project::class.java) }
                onResult(projects)
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to fetch projects") }
    }

    fun likeProject(projectId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()

        // Update the "likedProjects" for the investor
        val likedProjectsRef = database.getReference("Users/$userId/likedProjects")
        likedProjectsRef.push().setValue(projectId).addOnSuccessListener {
            println("Project $projectId liked successfully.")

            // Alert the founder of the project
            alertFounder(projectId, userId)
        }.addOnFailureListener {
            println("Failed to like project: ${it.message}")
        }
    }

    private fun alertFounder(projectId: String, investorId: String) {
        val database = FirebaseDatabase.getInstance()

        // Fetch project details to get the founder ID
        val projectRef = database.getReference("Projects/$projectId")
        projectRef.get().addOnSuccessListener { projectSnapshot ->
            val founderId = projectSnapshot.child("founderId").value as? String ?: return@addOnSuccessListener

            // Add the investor's profile to the "likedBy" list of the founder
            val likedByRef = database.getReference("Users/Founders/$founderId/likedBy/$investorId")
            likedByRef.setValue(true).addOnSuccessListener {
                println("Founder $founderId alerted successfully.")

                // Optionally, send a notification to the founder
                sendNotificationToFounder(founderId, investorId)
            }.addOnFailureListener {
                println("Failed to alert founder: ${it.message}")
            }
        }.addOnFailureListener {
            println("Failed to fetch project details: ${it.message}")
        }
    }

    private fun sendNotificationToFounder(founderId: String, investorId: String) {
        // Hypothetical notification logic
        println("Notification sent to Founder $founderId about Investor $investorId liking the project.")
        // Integrate a notification service like Firebase Cloud Messaging (FCM) for real notifications
    }

    fun dislikeProject(projectId: String) {
        updateDatabaseWithDecision(projectId, "dislikedProjects")
    }

    private fun updateDatabaseWithDecision(projectId: String, path: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Users/Investors/$userId/$path")
        reference.push().setValue(projectId)
    }
}