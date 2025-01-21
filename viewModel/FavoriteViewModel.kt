package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import com.example.invest.data.InvestorProfile
import com.example.invest.data.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoriteViewModel : ViewModel() {

    private val _favorites = MutableStateFlow<List<Project>>(emptyList())
    val favorites: StateFlow<List<Project>> get() = _favorites

    init {
        fetchUserFavorites(
            onResult = { fetchedFavoritesProjects ->
                _favorites.value = fetchedFavoritesProjects
            },
            onFailure = { errorMessage ->
                println("Error fetching projects: $errorMessage")
            }
        )
    }

    private fun fetchUserFavorites(
        onResult: (List<Project>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val favoriteProjectsRef = database.getReference("Users/$userId/favoriteProjects")

        favoriteProjectsRef.get().addOnSuccessListener { snapshot ->
            val projectIds = snapshot.children.mapNotNull { it.value as? String }
            val projects = mutableListOf<Project>()

            projectIds.forEach { projectId ->
                val projectRef = database.getReference("Projects/$projectId")
                projectRef.get().addOnSuccessListener { projectSnapshot ->
                    val project = projectSnapshot.getValue(Project::class.java)
                    if (project != null) {
                        projects.add(project)

                        if (projects.size == projectIds.size && project.favoriteBy?.containsKey(userId) == true) {
                            onResult(projects)
                        }
                    }
                }.addOnFailureListener {
                    println("Failed to fetch project details for $projectId: ${it.message}")
                }
            }
        }.addOnFailureListener {
            onFailure(it.message ?: "Failed to fetch favorite project IDs")
        }
    }

    fun likeProject(project: Project) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()

        val likedProjectsRef = database.getReference("Users/$userId/likedProjects")
        likedProjectsRef.child(project.id).setValue(true).addOnSuccessListener {
            println("Project ${project.id} liked successfully.")
            removeProjectFromFavorites(project.id)
        }.addOnFailureListener {
            println("Failed to like project: ${it.message}")
        }

        val likedByRef = database.getReference("Projects/${project.id}/likedBy")
        likedByRef.child(userId).setValue(true).addOnSuccessListener {
            println("Project ${project.id} likedby attribute done $userId.")
        }.addOnFailureListener { error ->
            println("Failed to update likedBy for project ${project.id}: ${error.message}")
        }
    }

    fun dislikeProject(project: Project) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()

        val dislikedProjectsRef = database.getReference("Users/$userId/dislikedProjects")
        dislikedProjectsRef.child(project.id).setValue(true).addOnSuccessListener {
            println("Project ${project.id} disliked successfully.")
            removeProjectFromFavorites(project.id)
        }.addOnFailureListener {
            println("Failed to dislike project: ${it.message}")
        }
    }

    private fun removeProjectFromFavorites(projectId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()

        val favoriteUserRef = database.getReference("Users/$userId/favoriteProjects/$projectId")
        val favoriteProjectsRef = database.getReference("Projects/$projectId/favoriteBy/$userId")

        favoriteUserRef.removeValue()
            .addOnSuccessListener {
                println("Project $projectId removed from user's favorites.")
            }
            .addOnFailureListener { error ->
                println("Failed to remove project $projectId from user's favorites: ${error.message}")
            }

        favoriteProjectsRef.removeValue()
            .addOnSuccessListener {
                println("User $userId removed from project's favoriteBy list.")
            }
            .addOnFailureListener { error ->
                println("Failed to remove user $userId from project's favoriteBy list: ${error.message}")
            }

        _favorites.value = _favorites.value.filterNot { it.id == projectId }
    }



}

