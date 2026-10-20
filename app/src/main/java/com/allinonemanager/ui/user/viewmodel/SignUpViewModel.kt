package com.allinonemanager.ui.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allinonemanager.data.remote.ApiClient
import com.allinonemanager.data.remote.UserApi
import com.allinonemanager.ui.user.screens.dto.SignUpRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

sealed interface SignUpUiState {
    data object Idle : SignUpUiState
    data object Loading : SignUpUiState
    data class Success(val message: String) : SignUpUiState
    data class Error(val message: String) : SignUpUiState
}

class SignUpViewModel(
    private val userApi: UserApi = ApiClient.userApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun signUp(request: SignUpRequest) {
        if (_uiState.value is SignUpUiState.Loading) return
        _uiState.value = SignUpUiState.Loading

        viewModelScope.launch {
            _uiState.value = try {
                val response = userApi.signUp(request)
                if (response.isSuccessful) {
                    val message = response.body()?.string()?.takeIf { it.isNotBlank() }
                        ?: "Account created. Please login."
                    SignUpUiState.Success(message)
                } else {
                    SignUpUiState.Error(parseErrorMessage(response.errorBody()?.string(), response.code()))
                }
            } catch (_: IOException) {
                SignUpUiState.Error("Unable to reach server. Check your connection.")
            } catch (e: Exception) {
                SignUpUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun resetState() {
        _uiState.value = SignUpUiState.Idle
    }

    // Uses the backend's "message" field when the error body is JSON, else falls back to the HTTP code
    private fun parseErrorMessage(body: String?, code: Int): String =
        body?.let { runCatching { JSONObject(it).optString("message") }.getOrNull() }
            ?.takeIf { it.isNotBlank() }
            ?: "Sign up failed ($code)"
}
