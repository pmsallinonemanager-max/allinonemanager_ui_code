package com.allinonemanager.ui.cc.screens.dto

// One item of GET /credit-cards/{userId}. Nullable because Gson leaves missing JSON fields as null.
data class CreditCard(
    val ccId: Int? = null,
    val userId: Int? = null,
    val ccNumber: String? = null,
    val ccBank: String? = null,
    val ccName: String? = null,
    val ccType: String? = null,
    val expDate: String? = null,
    val cvv: String? = null,
    val nameOnCard: String? = null,
    val statementDate: String? = null,
    val dueDate: String? = null,
    val ccLimit: String? = null
) {
    // "•••• 1234" so the full number is not shown in lists
    val maskedNumber: String
        get() = "•••• " + ccNumber.orEmpty().takeLast(4)
}
