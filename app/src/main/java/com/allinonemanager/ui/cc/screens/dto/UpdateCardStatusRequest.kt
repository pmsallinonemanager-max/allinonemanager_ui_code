package com.allinonemanager.ui.cc.screens.dto

// Body of PUT /credit-cards/status: deactivate (false) or reactivate (true) a card
data class UpdateCardStatusRequest(
    val ccId: Int,
    val userId: Int,
    val isActive: Boolean
)
