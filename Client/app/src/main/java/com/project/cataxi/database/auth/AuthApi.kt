package com.project.cataxi.database.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("registration")
    fun registration(@Body request: RegistrationRequest): Call<AuthResponse>
}