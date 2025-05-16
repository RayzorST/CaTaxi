package com.project.cataxi.database.orders

data class OrdersGetRequest(
        val user: String
)

data class OrdersPostRequest(
        val typeCar: String,
        val pointA: String,
        val pointB: String,
        val user: String
)

data class OrderResponse(
        val id: String,
        val createAt: String,
        val closedAt: String?,
        val typeCar: String,
        val pointA: String,
        val pointB: String,
        val user: String
)

data class OrdersResponse(
        val orders: ArrayList<OrderResponse>
)