package com.example.spotechnify.repository

class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val prefs: SharedPrefs
) {
    // ... existing login and signup functions ...

    fun hasAccessToken(): Boolean {
        return prefs.getAccessToken() != null
    }

    fun clearAuthData() {
        prefs.clearAuthData()
    }

    // Add this function to validate tokens if needed
    suspend fun validateToken(): Boolean {
        return try {
            val token = prefs.getAccessToken() ?: return false
            val response = api.validateToken(TokenValidationRequest(token))
            response.isSuccessful && response.body()?.isValid == true
        } catch (e: Exception) {
            false
        }
    }
}