package com.example.invest.data

data class InvestorProfile(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val investmentBudget: String = "",
    val profileImage: String = "",
    val profileType: String = "",
    var investmentHorizon: String = "",
    var startupStageInterest: String = "",
    var investedCompanies: List<String> = emptyList(),
    var createdAt: Long = 0L
)
