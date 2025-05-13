package com.project.order

import com.project.database.orders.OrderDTO
import com.project.database.orders.Orders
import com.project.login.LoginResponseRemote
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.UUID

fun Application.configureOrderRouting() {
    routing {
        post("/order") {
            val receive = call.receive<OrderResponseRemote>()

            val id = UUID.randomUUID().toString()
            val createAt = receive.createAt
            val closedAt = receive.closedAt
            val typeCar = receive.typeCar
            val pointA = receive.pointA
            val pointB = receive.pointB
            val user = receive.user

            try {
                Orders.insert(
                    OrderDTO(
                        rowId = id,
                        closedAt = closedAt,
                        typeCar = typeCar,
                        pointA = pointA,
                        pointB = pointB,
                        user = user
                    )
                )
            }
            catch (e: Exception){
                call.respond(HttpStatusCode.Conflict, e.toString())
            }
            call.respond(OrderResponseRemote(id, createAt, closedAt, typeCar, pointA, pointB, user))

        }

        post("/order-get") {
            val receive = call.receive<OrderReceiveRemote>()
            val ordersRR: ArrayList<OrderResponseRemote> = arrayListOf()
            try{
                val ordersDTO = Orders.fetchOrders(receive.user)
                for (item in ordersDTO){
                    ordersRR.add(OrderResponseRemote(item.rowId, item.createdAt, item.closedAt, item.typeCar, item.pointA, item.pointB, item.user))
                }
            }
            catch (e: Exception){
                call.respond(HttpStatusCode.Conflict, e.toString())
            }
            call.respond(OrdersResponseRemote(ordersRR))
        }
    }
}