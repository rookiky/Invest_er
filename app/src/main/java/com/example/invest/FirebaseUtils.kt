package com.example.invest

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
