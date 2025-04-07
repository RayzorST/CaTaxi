package com.project.registration

import com.project.database.tokens.TokenDTO
import com.project.database.tokens.Tokens
import com.project.database.users.UserDTO
import com.project.database.users.Users
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.util.UUID

fun Application.configureRegistrationRouting() {
    routing {
        post("/registration") {
            val receive = call.receive<RegistrationReceiveRemote>()

            val userDTO = Users.fetchUser(receive.email)
            if (userDTO != null){
                call.respond(HttpStatusCode.Conflict, "User already exist")
            }
            else{
                val token = UUID.randomUUID().toString()
                try {
                    Users.insert(
                        UserDTO(
                            email = receive.email,
                            password = receive.password,
                            firstName = receive.firstName,
                            secondName = receive.secondName
                        )
                    )
                }
                catch (e: Exception){
                    call.respond(HttpStatusCode.Conflict, e.toString())
                }
                Tokens.insert(
                    TokenDTO(
                        rowId = UUID.randomUUID().toString(),
                        email = receive.email,
                        token = token
                    )
                )
                call.respond(RegistrationResponseRemote(token))
            }
        }
    }
}