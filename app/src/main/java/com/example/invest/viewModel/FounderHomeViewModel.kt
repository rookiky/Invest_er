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
        val founderProjectsRef = database.getReference("Users/$userId/likedBy")

        founderProjectsRef.get()
            .addOnSuccessListener { snapshot ->
                println(userId)
                val investors = snapshot.children.mapNotNull { it.getValue(InvestorProfile::class.java) }
                onResult(investors)
            }
            .addOnFailureListener { onFailure(it.message ?: "Failed to fetch projects") }
    }

    fun likeInvestor(investorId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()

        val likedInvestorsRef = database.getReference("Users/$userId/likedProjects")
        likedInvestorsRef.push().setValue(investorId).addOnSuccessListener {
            println("Investor $investorId liked successfully.")

            alertInvestor(investorId, userId)
        }.addOnFailureListener {
            println("Failed to like investor: ${it.message}")
        }
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

