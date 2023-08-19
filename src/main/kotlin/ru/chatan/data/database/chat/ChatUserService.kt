package ru.chatan.data.database.chat

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.chatan.data.database.chat._enum.ChatUserRole
import ru.chatan.data.database.chat.models.ChatUserModel
import ru.chatan.data.database.dbQuery

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

    suspend fun fetch(userId: Long): List<ChatUserModel> {
        return dbQuery {
            ChatUser.select(where = { ChatUser.userId eq userId }).map {
                ChatUserModel(
                    id = it[ChatUser.id],
                    userId = userId,
                    chatId = it[ChatUser.chatId],
                    role = ChatUserRole.valueOf(it[ChatUser.role])
                )
            }
        }
    }

}