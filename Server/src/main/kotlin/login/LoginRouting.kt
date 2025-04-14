package com.project.login

import com.project.database.tokens.TokenDTO
import com.project.database.tokens.Tokens
import com.project.database.users.Users
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.util.UUID

fun Application.configureLoginRouting() {
    routing {
        post("/login") {
            val receive = call.receive<LoginReceiveRemote>()
            val userDTO = Users.fetchUser(receive.email)

            if (userDTO == null){
                call.respond(HttpStatusCode.BadRequest, "User Not Found")
            }
            else{
                if (userDTO.password == receive.password){
                    val token = UUID.randomUUID().toString()
                    val firstName = userDTO.firstName
                    val secondName = userDTO.secondName
                    Tokens.insert(
                        TokenDTO(
                        rowId = UUID.randomUUID().toString(),
                        email = receive.email,
                        token = token
                    )
                    )
                    call.respond(LoginResponseRemote(token, firstName, secondName))
                }
                else{
                    call.respond(HttpStatusCode.BadRequest, "Invalid Password")
                }
            }
        }
    }
}