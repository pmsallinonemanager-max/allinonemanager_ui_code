package com.allinonemanager.ui.cc.screens.dto

// Dates are yyyy-MM-dd, the backend parses them as LocalDate
data class AddDueDetailsRequest(
    val ccId: Int,
    val dueAmt: Double,
    val minimumDue: Double,
    val totalOutstanding: String,
    val statementDate: String,
    val dueDate: String
)
