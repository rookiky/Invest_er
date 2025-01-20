package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import com.example.invest.data.FounderProfile
import com.example.invest.utils.fetchFounders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InvestorHomeViewModel : ViewModel() {
    private val _founders = MutableStateFlow<List<FounderProfile>>(emptyList())
    val founders: StateFlow<List<FounderProfile>> = _founders

    init {
        fetchFounders { fetchedFounders ->
            _founders.value = fetchedFounders
        }
    }

    fun likeFounder(founderId: String) {
        updateDatabaseWithDecision(founderId, "likedProfiles")
    }

    fun dislikeFounder(founderId: String) {
        updateDatabaseWithDecision(founderId, "dislikedProfiles")
    }

    private fun updateDatabaseWithDecision(founderId: String, path: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Users/Investors/$userId/$path")
        reference.push().setValue(founderId)
    }
}