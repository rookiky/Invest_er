package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import com.example.invest.data.FounderProfile
import com.example.invest.data.Project
import com.example.invest.utils.fetchFounders
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

    private fun fetchProjects(
        onResult: (List<Project>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance()
        val founderProjectsRef = database.getReference("Projects")
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        founderProjectsRef.get()
            .addOnSuccessListener { snapshot ->
                val projects = snapshot.children.mapNotNull { data ->
                    val project = data.getValue(Project::class.java)
                    if (project != null && project.likedBy?.containsKey(userId) != true && project.favoriteBy?.containsKey(userId) != true  && project.dislikedBy?.containsKey(userId) != true) {
                        project
                    } else {
                        null
                    }
                }
                onResult(projects)
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to fetch projects") }
    }



    fun likeProject(projectId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()


        val likedProjectsRef = database.getReference("Users/$userId/likedProjects")
        likedProjectsRef.push().setValue(projectId).addOnSuccessListener {
            println("Project $projectId liked successfully.")

            notifyFounderAboutLike(projectId, userId)

        }.addOnFailureListener {
            println("Failed to like project: ${it.message}")
        }

        val likedByRef = database.getReference("Projects/$projectId/likedBy")
        likedByRef.child(userId).setValue(true).addOnSuccessListener {
            println("Project $projectId liked successfully by $userId.")
        }.addOnFailureListener { error ->
            println("Failed to update likedBy for project $projectId: ${error.message}")
        }



   }

    fun addFavoriteProject(projectId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()

        val favoriteProjectRef = database.getReference("Users/$userId/favoriteProjects")
        favoriteProjectRef.push().setValue(projectId).addOnSuccessListener {
            println("Project $projectId added to Favorites.")
            val projectFavoriteByRef = database.getReference("Projects/$projectId/favoriteBy")
            println("rules reference: $projectFavoriteByRef")
            println("userId: $userId")
            println("projectId: $projectId")
            projectFavoriteByRef.child(userId).setValue(true)
                .addOnSuccessListener {
                println("Project $projectId marked as favorite by user $userId.")
            }
                .addOnFailureListener { error ->
                println("Failed to update favoriteBy for project $projectId: ${error.message}")
            }

        }.addOnFailureListener { error ->
            println("Failed to add project $projectId to Favorites: ${error.message}")
        }
    }


    fun dislikeProject(projectId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()


        val likedProjectsRef = database.getReference("Users/$userId/dislikedProjects")
        likedProjectsRef.push().setValue(projectId).addOnSuccessListener {
            println("Project $projectId Disliked successfully.")

        }.addOnFailureListener {
            println("Failed to Dislike project: ${it.message}")
        }

        val likedByRef = database.getReference("Projects/$projectId/dislikedBy")
        likedByRef.child(userId).setValue(true).addOnSuccessListener {
            println("Project $projectId liked successfully by $userId.")
        }.addOnFailureListener { error ->
            println("Failed to update likedBy for project $projectId: ${error.message}")
        }
    }


    private fun notifyFounderAboutLike(projectId: String, investorId: String) {
        val database = FirebaseDatabase.getInstance()
        val projectRef = database.getReference("Projects/$projectId")

        projectRef.get().addOnSuccessListener { snapshot ->
            val founderId = snapshot.child("founderId").getValue(String::class.java)
            if (founderId != null) {
                val notificationRef = database.getReference("Users/$founderId/notifications").push()

                val notificationData = mapOf(
                    "title" to "New Like on Your Project!",
                    "message" to "An investor liked your project with ID: $projectId.",
                    "investorId" to investorId,
                    "projectId" to projectId,
                    "timestamp" to System.currentTimeMillis()
                )

                notificationRef.setValue(notificationData).addOnSuccessListener {
                    println("Notification sent to founder $founderId for project $projectId.")
                }.addOnFailureListener { error ->
                    println("Failed to send notification to founder: ${error.message}")
                }
            } else {
                println("Founder ID not found for project $projectId.")
            }
        }.addOnFailureListener { error ->
            println("Failed to fetch project details for notification: ${error.message}")
        }
    }

}