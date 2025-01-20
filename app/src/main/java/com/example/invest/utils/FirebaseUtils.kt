package com.example.invest.utils

import com.example.invest.data.FounderProfile
import com.example.invest.data.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

fun fetchFounders(onFoundersFetched: (List<FounderProfile>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val foundersRef = database.getReference("Users/Founders")

    foundersRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val founderList = mutableListOf<FounderProfile>()
            snapshot.children.forEach { data ->
                val founder = data.getValue(FounderProfile::class.java)
                if (founder != null) {
                    founderList.add(founder)
                }
            }
            onFoundersFetched(founderList)
        }

        override fun onCancelled(error: DatabaseError) {
            println("Error fetching founders: ${error.message}")
        }
    })
}

fun fetchProjects(onProjectsFetched: (List<Project>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val projectsRef = database.getReference("Projects")

    projectsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val projectList = mutableListOf<Project>()
            snapshot.children.forEach { data ->
                val project = data.getValue(Project::class.java)
                if (project != null) {
                    projectList.add(project)
                    println("Fetched Project: ${project.name}")
                } else {
                    println("Invalid project data at key: ${data.key}")
                }
            }
            println("Total Projects Fetched: ${projectList.size}")
            onProjectsFetched(projectList)
        }

        override fun onCancelled(error: DatabaseError) {
            println("Error fetching projects: ${error.message}")
        }
    })
}

fun fetchProjectsForInvestors(
    onResult: (List<Project>) -> Unit,
    onFailure: (String) -> Unit
) {
    val database = FirebaseDatabase.getInstance()
    val founderProjectsRef = database.getReference("Projects")

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



fun likeFounderProfile(founderId: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val database = FirebaseDatabase.getInstance()

    // Add the investor ID to the founder's "likedBy" list
    val founderLikesRef = database.getReference("Users/Founders/$founderId/likedBy")
    founderLikesRef.child(userId).setValue(true)

    // Optionally, add the founder ID to the investor's "likedProfiles" list
    val investorLikesRef = database.getReference("Users/Investors/$userId/likedProfiles")
    investorLikesRef.child(founderId).setValue(true)
}


fun fetchAccountType(userId: String, onResult: (String) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("Users/$userId/profileType")

    userRef.get().addOnSuccessListener { snapshot ->
        val accountType = snapshot.getValue(String::class.java) ?: "Unknown"
        onResult(accountType)
    }.addOnFailureListener {
        println("Failed to fetch account type: ${it.message}")
        onResult("Unknown")
    }
}


