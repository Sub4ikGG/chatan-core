package ru.chatan.data.database.chat

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import ru.chatan.data.database.user.UserService

class ChatService(private val database: Database) {

    object Chat : Table() {
        val id = long("id").autoIncrement()
        val code = varchar("code", 128)
        val name = varchar("name", 128)
        val description = varchar("description", 256)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Chat)
        }
    }

}