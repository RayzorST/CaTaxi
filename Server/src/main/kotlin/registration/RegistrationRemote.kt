package com.project.registration

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationReceiveRemote(
    val email: String,
    val password: String,
    val firstName: String,
    val secondName: String
)

@Serializable
data class RegistrationResponseRemote(
    val token: String
)