package com.example.invest.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.invest.data.InvestorProfile
import com.example.invest.data.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InvestorProfileViewModel: ViewModel() {

    private val _profile = MutableStateFlow(InvestorProfile())
    val profile: StateFlow<InvestorProfile> get() = _profile

    private val database = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    init {
        fetchInvestorProfile()
    }

    private fun fetchInvestorProfile() {
        if (userId == null) return
        val profileRef = database.getReference("Users/$userId")

        profileRef.get().addOnSuccessListener { snapshot ->
            val investorProfile = snapshot.getValue(InvestorProfile::class.java)
            if (investorProfile != null) {
                _profile.value = investorProfile
            }
        }.addOnFailureListener {
            println("Failed to fetch investor profile: ${it.message}")
        }
    }

    fun updateProfile(updatedProfile: InvestorProfile, onComplete: (Boolean) -> Unit) {
        if (userId == null) return
        val profileRef = database.getReference("Users/$userId")

        profileRef.setValue(updatedProfile).addOnSuccessListener {
            _profile.value = updatedProfile
            onComplete(true)
        }.addOnFailureListener {
            println("Failed to update profile: ${it.message}")
            onComplete(false)
        }
    }
}
