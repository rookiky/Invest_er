package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.invest.data.InvestorProfile
import com.example.invest.data.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class FounderHomeViewModel : ViewModel() {
    private val _investors = MutableStateFlow<List<InvestorProfile>>(emptyList())
    val investors: StateFlow<List<InvestorProfile>> get() = _investors

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    init {
        fetchInvestorsWhoLikedProjects(
            onResult = { fetchedInvestors ->
                _investors.value = fetchedInvestors
            },
            onFailure = { errorMessage ->
                println("Error fetching projects: $errorMessage")
            }
        )
    }

    fun fetchInvestorsWhoLikedProjects(
        onResult: (List<InvestorProfile>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val founderProjectsRef = database.getReference("Users/$userId/projects")

        founderProjectsRef.get()
            .addOnSuccessListener { snapshot ->
                val projectIds = snapshot.children.mapNotNull { it.key }
                val investorsList = mutableListOf<InvestorProfile>()

                projectIds.forEach { projectId ->
                    val likedByRef = database.getReference("Projects/$projectId/likedBy")

                    likedByRef.get()
                        .addOnSuccessListener { likedBySnapshot ->
                            likedBySnapshot.children.forEach { investorSnapshot ->
                                val investorId = investorSnapshot.key ?: return@forEach
                                fetchInvestorProfile(investorId) { investor ->
                                    investorsList.add(investor)
                                    _investors.value = investorsList
                                }
                            }
                        }
                        .addOnFailureListener {
                            println("Failed to fetch likedBy for project $projectId: ${it.message}")
                        }
                }
            }
            .addOnFailureListener {
                println("Failed to fetch founder's projects: ${it.message}")
            }
    }

    private fun fetchInvestorProfile(userId: String, onResult: (InvestorProfile) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val investorRef = database.getReference("Users/$userId")

        investorRef.get()
            .addOnSuccessListener { snapshot ->
                val investor = snapshot.getValue(InvestorProfile::class.java)
                if (investor != null) {
                    onResult(investor)
                }
            }
            .addOnFailureListener {
                println("Failed to fetch investor profile: ${it.message}")
            }
    }

    fun likeInvestor(investorId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()

        val likedProfilesRef = database.getReference("Users/$userId/likedProfiles")
        likedProfilesRef.push().setValue(investorId).addOnSuccessListener {
            println("Investor $investorId liked successfully.")

            startChat(userId, investorId,
                onSuccess = { chatId ->
                    println("Chat started successfully with ID: $chatId")
                },
                onFailure = { error ->
                    println("Failed to start chat: $error")
                }
            )
        }.addOnFailureListener {
            println("Failed to like investor: ${it.message}")
        }
    }

    fun rejectInvestor(investorId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val rejectedProfilesRef = database.getReference("Users/$userId/rejectedProfiles")
        rejectedProfilesRef.push().setValue(investorId).addOnSuccessListener {
            println("Investor $investorId rejected successfully.")
        }.addOnFailureListener {
            println("Failed to reject investor: ${it.message}")
        }
    }

    fun startChat(founderId: String, investorId: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val chatId = database.getReference("Chats").push().key ?: return

        val chatData = mapOf(
            "participants" to mapOf(
                founderId to true,
                investorId to true
            ),
            "lastMessage" to "",
            "timestamp" to System.currentTimeMillis()
        )

        database.getReference("Chats/$chatId").setValue(chatData)
            .addOnSuccessListener { onSuccess(chatId) }
            .addOnFailureListener { onFailure(it.message ?: "Failed to start chat") }
    }

    private fun alertInvestor(founderId: String, investorId: String) {
        val database = FirebaseDatabase.getInstance()

        val investorRef = database.getReference("Projects/$")
        investorRef.get().addOnSuccessListener { investorSnapshot ->
            val userId = investorSnapshot.child("userId").value as? String ?: return@addOnSuccessListener

            val likedByRef = database.getReference("Users/$userId/likedBy/$investorId")
            likedByRef.setValue(true).addOnSuccessListener {
                println("Founder $userId alerted successfully.")

                sendNotificationToInvestor(userId, investorId)
            }.addOnFailureListener {
                println("Failed to alert founder: ${it.message}")
            }
        }.addOnFailureListener {
            println("Failed to fetch project details: ${it.message}")
        }
    }

    private fun sendNotificationToInvestor(founderId: String, investorId: String) {
        println("Notification sent to Investor $investorId about Founder $founderId liking the profile.")
    }

    fun dislikeInvestor(investorId: String) {
        updateDatabaseWithDecision(investorId, "dislikedProjects")
    }

    private fun updateDatabaseWithDecision(projectId: String, path: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Users/Investors/$userId/$path")
        reference.push().setValue(projectId)
    }


}

