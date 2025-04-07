package com.project.database.users

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table("users") {
    private val firstName = Users.varchar("firstname", 25)
    private val secondName = Users.varchar("secondname", 25)
    private val email = Users.varchar("email", 25)
    private val password = Users.varchar("password", 25)

    fun insert(userDTO: UserDTO) {
        transaction {
            Users.insert {
                it[email] = userDTO.email
                it[password] = userDTO.password
                it[firstName] = userDTO.firstName
                it[secondName] = userDTO.secondName
            }
        }
    }

    fun fetchUser(email: String): UserDTO? {
        return try {
            transaction {
                Users.selectAll().where { Users.email eq email }.singleOrNull()?.let { row ->
                    UserDTO(
                        email = row[Users.email],
                        password = row[password],
                        firstName = row[firstName],
                        secondName = row[secondName]
                    )
                }
            }
        }
        catch (e: Exception){
            null
        }
    }
}