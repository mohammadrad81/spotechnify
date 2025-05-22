package com.example.spotechnify.repository

class AuthRepository @Inject constructor(
    private val api: AuthApi, // Your API interface
    private val prefs: SharedPrefs
) {
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    prefs.saveAccessToken(authResponse.accessToken)
                    Result.success(authResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(username: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.signUp(SignUpRequest(username, email, password))
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    prefs.saveAccessToken(authResponse.accessToken)
                    Result.success(authResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Sign up failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun hasAccessToken(): Boolean {
        return prefs.getAccessToken() != null
    }
}