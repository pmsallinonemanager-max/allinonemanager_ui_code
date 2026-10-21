package com.allinonemanager.ui.cc.screens.dto

// One item of GET /credit-cards/due/{ccId} and /credit-cards/due/user/{userId}.
// Nullable because Gson leaves missing JSON fields as null. Dates are yyyy-MM-dd.
data class DueDetail(
    val ccDueId: Int? = null,
    val ccId: Int? = null,
    val ccName: String? = null,
    val ccBank: String? = null,
    val ccNumberLast4: String? = null,
    val statementDate: String? = null,
    val dueDate: String? = null,
    val dueAmt: Double? = null,
    val minimumDue: Double? = null,
    val totalOutstanding: Double? = null,
    val isPaid: Boolean? = null
) {
    // "Monthly Treats •••• 6226"
    val cardLabel: String
        get() = listOfNotNull(ccName?.takeIf { it.isNotBlank() }, ccNumberLast4?.let { "•••• $it" })
            .joinToString(" ")
}
