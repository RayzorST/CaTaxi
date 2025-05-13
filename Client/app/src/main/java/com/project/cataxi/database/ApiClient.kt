package com.project.cataxi.database

import com.project.cataxi.database.auth.AuthApi
import com.project.cataxi.database.orders.OrdersApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)

    val ordersApi: OrdersApi = retrofit.create(OrdersApi::class.java)
}