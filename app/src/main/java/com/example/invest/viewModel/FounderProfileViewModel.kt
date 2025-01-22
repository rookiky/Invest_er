package com.example.invest.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.invest.data.FounderProfile
import com.example.invest.data.InvestorProfile
import com.example.invest.data.Project
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FounderProfileViewModel : ViewModel() {
    private val _profile = MutableStateFlow(FounderProfile())
    val profile: StateFlow<FounderProfile> get() = _profile

    private val database = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    fun fetchFounderProfile(founderId: String, onResult: (FounderProfile) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val founderRef = database.getReference("Users/$founderId")

        founderRef.get().addOnSuccessListener { snapshot ->
            val profile = snapshot.getValue(FounderProfile::class.java)
            if (profile != null) {
                onResult(profile)
            }
        }.addOnFailureListener { error ->
            Log.e("FetchProfile", "Failed to fetch founder profile: ${error.message}")
        }
    }

    fun updateProfile(updatedProfile: FounderProfile, onComplete: (Boolean) -> Unit) {
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
