package com.example.invest.utils

import com.example.invest.data.FounderProfile
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