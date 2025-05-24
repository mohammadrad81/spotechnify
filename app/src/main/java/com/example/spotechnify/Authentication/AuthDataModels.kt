package com.example.spotechnify.Authentication

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class AuthResponse(
    val message: String? = null,
    val access: String,
    val user: User,
    val detail: String? = null
)

data class User(
    val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val username: String,
    val email: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class SignUpRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val email: String,
    val password: String,
    val username: String
)