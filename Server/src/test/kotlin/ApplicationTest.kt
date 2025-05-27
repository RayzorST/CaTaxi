package com.project

import com.project.database.orders.OrderDTO
import com.project.database.tokens.TokenDTO
import com.project.database.users.UserDTO
import com.project.login.LoginReceiveRemote
import com.project.registration.RegistrationReceiveRemote
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testUserRegistration() = testApplication {
        application { module() }

        val response = client.post("/registration") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(RegistrationReceiveRemote(
                email = "test@example.com",
                password = "secure123",
                firstName = "Иван",
                secondName = "Иванов"
            )))
        }

        assertEquals(HttpStatusCode.Conflict, response.status)
    }

    @Test
    fun testUserLogin() = testApplication {
        application { module() }
        val loginData = mapOf(
            "email" to "test@example.com",
            "password" to "secure123"
        )

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(LoginReceiveRemote("test@example.com", "secure123")))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val tokenDTO = json.decodeFromString<TokenDTO>(response.bodyAsText())
        assertNotNull(tokenDTO.token)
        assertEquals("test@example.com", tokenDTO.email)
    }

    @Test
    fun testCreateOrder() = testApplication {
        application { module() }
        // Сначала получаем токен
        val token = loginAndGetToken()

        val orderDTO = OrderDTO(
            rowId = "order-1",
            typeCar = "Седан",
            pointA = "Москва, ул. Тверская",
            pointB = "Москва, аэропорт Шереметьево",
            user = "user-1"
        )

        val response = client.post("/order") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(json.encodeToString(orderDTO))
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun testGetUserOrders() = testApplication {
        application { module() }
        val token = loginAndGetToken()

        val response = client.get("/order-get") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val orders = json.decodeFromString<List<OrderDTO>>(response.bodyAsText())
        assertTrue(orders.isNotEmpty())
    }

    @Test
    fun testUnauthorizedAccess() = testApplication {
        application { module() }
        val response = client.get("/order-get")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    private suspend fun ApplicationTestBuilder.loginAndGetToken(): String {
        val loginData = mapOf(
            "email" to "test@example.com",
            "password" to "secure123"
        )

        val response = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(loginData))
        }

        return json.decodeFromString<TokenDTO>(response.bodyAsText()).token
    }
}

