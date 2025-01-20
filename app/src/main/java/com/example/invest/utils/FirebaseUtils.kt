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


