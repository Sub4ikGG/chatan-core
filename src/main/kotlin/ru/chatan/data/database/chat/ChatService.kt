package ru.chatan.data.database.chat

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.chatan.data.database.chat.models.ChatModel
import ru.chatan.data.database.dbQuery

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

    suspend fun fetch(chatId: List<Long>): List<ChatModel> {
        return dbQuery {
            Chat.select(where = { Chat.id inList chatId }).map {
                ChatModel(
                    id = it[Chat.id],
                    code = it[Chat.code],
                    name = it[Chat.name],
                    description = it[Chat.description]
                )
            }
        }
    }

    suspend fun create(name: String, description: String, code: String): Long {
        return dbQuery {
            Chat.insert {
                it[Chat.name] = name
                it[Chat.code] = code
                it[Chat.description] = description
            }[Chat.id]
        }
    }

}