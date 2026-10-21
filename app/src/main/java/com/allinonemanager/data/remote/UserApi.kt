package com.allinonemanager.data.remote

import com.allinonemanager.ui.user.screens.dto.LoginRequest
import com.allinonemanager.ui.user.screens.dto.SignUpRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    // Relative to BASE_URL, e.g. http://10.0.2.2:8087/aiom/users/signup
    // Backend returns a plain-text message, e.g. "User registered successfully."
    @POST("users/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<ResponseBody>

    // Backend returns HTTP 200 for both outcomes with {"message": "...", "userId": 5}:
    // message "Login successful." on success, anything else is the failure reason (userId null)
    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<ResponseBody>
}
