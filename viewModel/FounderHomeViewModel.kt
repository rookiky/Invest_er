package com.example.invest.viewModel

import androidx.lifecycle.ViewModel
import com.example.invest.data.ChatRoom
import com.example.invest.data.InvestorProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FounderHomeViewModel : ViewModel() {
    private val _investors = MutableStateFlow<List<InvestorProfile>>(emptyList())
    val investors: StateFlow<List<InvestorProfile>> get() = _investors

    init {
        fetchInvestorsWhoLikedProjects()
    }

    private fun fetchInvestorsWhoLikedProjects() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        val likedProfilesRef = database.getReference("Users/$userId/likedProfiles")
        val projectsRef = database.getReference("Users/$userId/projects")

        likedProfilesRef.get().addOnSuccessListener { likedProfilesSnapshot ->
            val likedInvestorIds = likedProfilesSnapshot.children.mapNotNull { it.getValue(String::class.java) }.toSet()

            projectsRef.get().addOnSuccessListener { projectSnapshot ->
                val projectIds = projectSnapshot.children.mapNotNull { it.key }
                val investorsList = mutableListOf<InvestorProfile>()

                projectIds.forEach { projectId ->
                    val likedByRef = database.getReference("Projects/$projectId/likedBy")

                    likedByRef.get().addOnSuccessListener { likedBySnapshot ->
                        likedBySnapshot.children.forEach { investorSnapshot ->
                            val investorId = investorSnapshot.key ?: return@forEach

                            if (!likedInvestorIds.contains(investorId)) {
                                fetchInvestorProfile(investorId) { investor ->
                                    investorsList.add(investor)
                                    _investors.value = investorsList
                                }
                            }
                        }
                    }.addOnFailureListener {
                        println("Failed to fetch likedBy for project $projectId: ${it.message}")
                    }
                }
            }.addOnFailureListener {
                println("Failed to fetch projects: ${it.message}")
            }
        }.addOnFailureListener {
            println("Failed to fetch liked profiles: ${it.message}")
        }
    }


    private fun fetchInvestorProfile(investorId: String, onResult: (InvestorProfile) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val investorRef = database.getReference("Users/$investorId")

        investorRef.get().addOnSuccessListener { snapshot ->
            var investor = snapshot.getValue(InvestorProfile::class.java)
            if (investor != null) {
                investor.id = investorId
                onResult(investor)
            }
        }.addOnFailureListener {
            println("Failed to fetch investor profile: ${it.message}")
        }
    }

    fun likeInvestor(investorId: String, onChatCreated: (String?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance()
        println("Investor ID passed to likeInvestor: $investorId")
        val likedProfilesRef = database.getReference("Users/$userId/likedProfiles")
        likedProfilesRef.push().setValue(investorId).addOnSuccessListener {
            println("Investor $investorId liked successfully.")

            val chatId = generateChatId(userId, investorId)
            startChat(chatId, userId, investorId) { success ->
                if (success) {
                    println("Chat room created successfully with ID: $chatId.")
                    onChatCreated(chatId)
                } else {
                    println("Failed to create chat room.")
                    onChatCreated(null)
                }
            }
        }.addOnFailureListener {
            println("Failed to like investor: ${it.message}")
            onChatCreated(null)
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

    private fun startChat(chatId: String, founderId: String, investorId: String, onChatCreated: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val chatRef = database.getReference("Chats/$chatId")
        println(chatRef)
        val chatRoom = ChatRoom(
            chatId = chatId,
            user1Id = founderId,
            user2Id = investorId,
            lastMessage = "",
            timestamp = System.currentTimeMillis()
        )

        chatRef.setValue(chatRoom).addOnSuccessListener {
            val founderChatRoomRef = database.getReference("Users/$founderId/chatRooms/$chatId")
            val investorChatRoomRef = database.getReference("Users/$investorId/chatRooms/$chatId")

            founderChatRoomRef.setValue(chatRoom)
            investorChatRoomRef.setValue(chatRoom)

            onChatCreated(true)
        }.addOnFailureListener { exception ->
            println("Failed to create chat room: ${exception.message}")
            onChatCreated(false)
        }
    }


    private fun generateChatId(user1Id: String, user2Id: String): String {
        return if (user1Id < user2Id) "$user1Id-$user2Id" else "$user2Id-$user1Id"
    }
}
