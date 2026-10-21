package com.allinonemanager.ui.cc.screens.dto

// Body of PUT /credit-cards/due/update. ccId must be the card the due belongs to. Dates are yyyy-MM-dd.
data class UpdateDueDetailsRequest(
    val ccDueId: Int,
    val ccId: Int,
    val dueAmt: Double,
    val minimumDue: Double,
    val totalOutstanding: Double,
    val statementDate: String,
    val dueDate: String
)
