package com.allinonemanager.ui.cc.screens.dto

data class AddCreditCardRequest(
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
