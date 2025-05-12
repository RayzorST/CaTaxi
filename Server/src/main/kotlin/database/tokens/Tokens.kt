package com.project.database.tokens

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object Tokens : Table("tokens") {
    private val id = varchar("id", 50)
    private val email = varchar("email", 25)
    private val token = varchar("token", 50)

    fun insert(tokenDTO: TokenDTO) {
        transaction {
            Tokens.insert {
                it[id] = tokenDTO.rowId
                it[email] = tokenDTO.email
                it[token] = tokenDTO.token
            }
        }
    }
}