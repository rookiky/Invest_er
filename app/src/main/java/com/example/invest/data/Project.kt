package com.example.invest.data

data class Project(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val founderId: String = "",
    val createdAt: Long = 0L,
    val likedBy: Map<String, Boolean>? = null,
    val dislikedBy: Map<String, Boolean>? = null,
    val favoriteBy: Map<String, Boolean>? = null
)
