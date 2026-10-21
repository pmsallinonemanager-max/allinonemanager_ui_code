package com.allinonemanager.ui.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allinonemanager.data.UserSession
import com.allinonemanager.data.remote.ApiClient
import com.allinonemanager.data.remote.UserApi
import com.allinonemanager.ui.user.screens.dto.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val message: String) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(
    private val userApi: UserApi = ApiClient.userApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(request: LoginRequest) {
        if (_uiState.value is LoginUiState.Loading) return
        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            _uiState.value = try {
                val response = userApi.login(request)
                if (response.isSuccessful) {
                    // Backend answers 200 for wrong credentials too, so the message decides.
                    // Current backend sends {"message","userId"}; older builds sent the message as plain text.
                    val body = response.body()?.string()?.trim().orEmpty()
                    val json = runCatching { JSONObject(body) }.getOrNull()
                    val message = json?.optString("message")?.takeIf { it.isNotBlank() } ?: body
                    val userId = json?.takeUnless { it.isNull("userId") }?.optInt("userId")
                    when {
                        message != SUCCESS_MESSAGE -> LoginUiState.Error(message.ifBlank { "Login failed" })
                        userId == null -> LoginUiState.Error("Server did not send userId. Restart the backend with the latest code.")
                        else -> {
                            UserSession.userId = userId
                            LoginUiState.Success(message)
                        }
                    }
                } else {
                    LoginUiState.Error("Login failed (${response.code()})")
                }
            } catch (_: IOException) {
                LoginUiState.Error("Unable to reach server. Check your connection.")
            } catch (e: Exception) {
                LoginUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }

    private companion object {
        const val SUCCESS_MESSAGE = "Login successful."
    }
}
