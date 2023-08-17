package ru.chatan.data.database.chat

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

class ChatConfigurationService(private val database: Database) {

    object ChatConfiguration: Table() {
        val id = long("id").autoIncrement()
        val chatId = long("chat_id")
        val userLimit = integer("user_limit")
        val state = varchar("state", 64)

        override val primaryKey: PrimaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(ChatConfiguration)
        }
    }

}