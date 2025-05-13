package com.project.token

import com.project.database.orders.Orders
import com.project.database.tokens.Tokens
import com.project.order.OrderResponseRemote
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureTokenRouting() {
    routing {
        post("/token") {
            val receive = call.receive<TokenReceiveRemote>()

            try{
                val verified = Tokens.fetch(receive.token, receive.email)
                if (verified == null || verified == false){
                    call.respond(TokenResponseRemote(false))
                }
                call.respond(TokenResponseRemote(true))
            }
            catch (e: Exception){
                call.respond(HttpStatusCode.Conflict, e.toString())
            }
        }
    }
}