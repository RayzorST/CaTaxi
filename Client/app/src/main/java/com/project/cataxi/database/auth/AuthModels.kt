package com.project.cataxi.database.auth

import kotlinx.serialization.Serializable

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegistrationRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val secondName: String
)

data class AuthResponse(
    val token: String,
    val firstName: String,
    val secondName: String
)

data class TokenRequest(
    val email: String,
    val token: String
)

data class TokenResponse(
    val verified: Boolean
)

data class ChangeUserNameRequest(
    val email: String,
    val firstName: String,
    val secondName: String
)

data class ChangeUserNameResponse(
    val firstName: String,
    val secondName: String
)
