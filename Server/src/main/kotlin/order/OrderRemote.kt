package com.project.order

import com.project.database.orders.Orders
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class OrderReceiveRemote(
    val user: String
)

@Serializable
data class OrderResponseRemote(
    val id: String? = null,
    val createAt: LocalDateTime? = null,
    val closedAt: LocalDateTime? = null,
    val typeCar: String,
    val pointA: String,
    val pointB: String,
    val user: String
)

@Serializable
data class OrdersResponseRemote(
    val orders: ArrayList<OrderResponseRemote>
)