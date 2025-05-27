package com.project.cataxi.database.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login-change")
    fun change(@Body request: ChangeUserNameRequest): Call<ChangeUserNameResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("registration")
    fun registration(@Body request: RegistrationRequest): Call<AuthResponse>

    @POST("token")
    fun token(@Body request: TokenRequest): Call<TokenResponse>
}