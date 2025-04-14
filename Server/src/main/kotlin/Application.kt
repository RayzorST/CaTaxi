package com.project

import com.project.login.configureLoginRouting
import com.project.registration.configureRegistrationRouting
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.jetbrains.exposed.sql.Database

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    Database.connect("jdbc:postgresql://localhost:5432/cataxi",
        "org.postgresql.Driver",
        "postgres",
        "Nafanya98"
    )

    configureSerialization()
    configureLoginRouting()
    configureRegistrationRouting()
}
