package com.project.token

import kotlinx.serialization.Serializable

@Serializable
data class TokenReceiveRemote(
    val token: String,
    val email: String
)

@Serializable
data class TokenResponseRemote(
    val verified: Boolean
)