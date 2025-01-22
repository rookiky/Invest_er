package com.example.invest.utils

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.invest.data.FounderProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

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

fun listenForNotifications(userId: String, onNotificationReceived: (String, String) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val notificationsRef = database.getReference("Users/$userId/notifications")

    notificationsRef.addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val title = snapshot.child("title").getValue(String::class.java) ?: "No Title"
            val message = snapshot.child("message").getValue(String::class.java) ?: "No Message"

            onNotificationReceived(title, message)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {
            println("Notification Listener Cancelled: ${error.message}")
        }
    })
}

@Composable
fun NotificationHandler(userId: String) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        listenForNotifications(userId) { title, message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "$title: $message",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}


