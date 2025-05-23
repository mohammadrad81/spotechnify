package com.example.spotechnify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel : ViewModel() {
    private val apiService = ApiService.create()

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    sealed class AuthResult {
        data class Success(val user: User, val token: String) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    fun signUp(firstName: String, lastName: String, email: String, password: String, username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.signUp(
                    SignUpRequest(firstName, lastName, email, password, username)
                )
                handleResponse(response)
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error("Network error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.login(LoginRequest(username, password))
                handleResponse(response)
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error("Network error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleResponse(response: Response<AuthResponse>) {
        if (response.isSuccessful) {
            val authResponse = response.body()
            authResponse?.let {
                if (it.detail != null) {
                    _authResult.value = AuthResult.Error(it.detail)
                } else {
                    _authResult.value = AuthResult.Success(it.user, it.access)
                }
            } ?: run {
                _authResult.value = AuthResult.Error("Unknown error occurred")
            }
        } else {
            try {
                val errorResponse = response.errorBody()?.string()
                    ?.let { Gson().fromJson(it, AuthResponse::class.java) }
                _authResult.value = AuthResult.Error(errorResponse?.detail ?: "Unknown error occurred")
            } catch (e: Exception) {
                _authResult.value = AuthResult.Error("Failed to parse error response")
            }
        }
    }

    fun resetAuthResult() {
        _authResult.value = null
    }
}