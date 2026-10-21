package com.allinonemanager.ui.cc.screens.dto

// Body of PUT /credit-cards/update; same fields as AddCreditCardRequest plus the card being changed
data class UpdateCreditCardRequest(
    val ccId: Int,
    val userId: Int,
    val ccNumber: String,
    val ccBank: String,
    val ccName: String,
    val ccType: String,
    val expDate: String,
    val cvv: String,
    val nameOnCard: String,
    val statementDate: String,
    val dueDate: String,
    val ccLimit: String
)
