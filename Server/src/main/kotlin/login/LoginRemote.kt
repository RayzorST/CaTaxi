package com.project.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginReceiveRemote(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponseRemote(
    val token: String,
    val firstName: String,
    val secondName: String
)

@Serializable
data class ChangeUserNameReceiveRemote(
    val email: String,
    val firstName: String,
    val secondName: String
)

@Serializable
data class ChangeUserNameResponseRemote(
    val firstName: String,
    val secondName: String
)