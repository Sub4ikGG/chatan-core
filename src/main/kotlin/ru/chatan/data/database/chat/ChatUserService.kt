package ru.chatan.data.database.chat

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

class ChatUserService(private val database: Database) {

    object ChatUser: Table() {
        val id = long("id").autoIncrement()
        val chatId = long("chat_id")
        val userId = long("user_id")
        val role = varchar("role", 128)
    }

    init {
        transaction(database) {
            SchemaUtils.create(ChatUser)
        }
    }

}