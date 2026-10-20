package com.allinonemanager.ui.user.screens.dto

data class SignUpRequest(
    val fullName: String,
    val mobile: String,
    val email: String,
    val password: String
)