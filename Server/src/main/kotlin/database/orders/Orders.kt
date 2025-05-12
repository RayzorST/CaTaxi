package com.project.database.orders

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.selectAll

object Orders : Table("orders") {
    private val id = varchar("id", 50)

    private val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    private val closedAt = datetime("closed_at").nullable()
    private val typeCar = varchar("type_car", 25)
    private val pointA = Orders.varchar("point_a", 50)
    private val pointB = Orders.varchar("point_b", 50)
    private val user = varchar("user", 50)

    fun insert(orderDTO: OrderDTO) {
        transaction {
            Orders.insert {
                it[id] = orderDTO.rowId
                it[createdAt] = CurrentDateTime
                it[closedAt] = orderDTO.closedAt
                it[typeCar] = orderDTO.typeCar
                it[pointA] = orderDTO.pointA
                it[pointB] = orderDTO.pointB
                it[user] = orderDTO.user
            }
        }
    }

    fun fetchOrders(user: String): List<OrderDTO> {
        return try {
            transaction {
                Orders.selectAll().where { Orders.user eq user }
                    .map {
                        OrderDTO(
                            rowId = it[Orders.id],
                            createdAt = it[Orders.createdAt],
                            closedAt = it[Orders.closedAt],
                            typeCar = it[Orders.typeCar],
                            pointA = it[Orders.pointA],
                            pointB = it[Orders.pointB],
                            user = it[Orders.user]
                        )
                    }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}