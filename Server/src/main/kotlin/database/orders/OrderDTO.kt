package com.project.database.orders

import kotlinx.datetime.LocalDateTime


class OrderDTO (
    val rowId: String,
    val createdAt: LocalDateTime? = null,
    val closedAt: LocalDateTime? = null,
    val typeCar: String,
    val pointA: String,
    val pointB: String,
    val user: String
)