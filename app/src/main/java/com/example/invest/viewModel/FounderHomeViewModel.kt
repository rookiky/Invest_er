package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.invest.data.InvestorProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class FounderHomeViewModel : ViewModel() {
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val _investors = mutableStateListOf(
        InvestorProfile("1", "Alice", "Building a green tech startup.", "500000"),
        InvestorProfile("2", "Bob", "Creating an AI platform.", "500000"),
        InvestorProfile("3", "Charlie", "Innovating in e-commerce.", "500000000")
    )
    val investors: SnapshotStateList<InvestorProfile> = _investors

    fun likeInvestor(investorId: String) {
        _investors.removeIf { it.id == investorId }
        userId?.let { uid ->
            val reference = FirebaseDatabase.getInstance().getReference("Users/$uid/likedProfiles")
            reference.push().setValue(investorId)
        }
    }

    fun dislikeInvestor(investorId: String) {
        _investors.removeIf { it.id == investorId }
        userId?.let { uid ->
            val reference = FirebaseDatabase.getInstance().getReference("Users/$uid/dislikedProfiles")
            reference.push().setValue(investorId)
        }
    }
}
