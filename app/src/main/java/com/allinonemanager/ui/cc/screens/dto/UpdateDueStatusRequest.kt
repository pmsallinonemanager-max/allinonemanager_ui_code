package com.allinonemanager.ui.cc.screens.dto

// Body of PUT /credit-cards/due/status
data class UpdateDueStatusRequest(
    val ccDueId: Int,
    val isPaid: Boolean
)
