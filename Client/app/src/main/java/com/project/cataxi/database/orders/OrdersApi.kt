package com.project.cataxi.database.orders

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OrdersApi {
    @POST("order")
    fun post(@Body request: OrdersPostRequest): Call<OrdersResponse>

    @POST("order-get")
    fun get(@Body request: OrdersGetRequest): Call<OrdersResponse>
}