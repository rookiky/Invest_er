package com.example.invest.data

data class InvestorProfile(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val investmentBudget: String = "",
    val profileImage: String = "",
    val profileType: String = "",
    val createdAt: Long = 0L
)
