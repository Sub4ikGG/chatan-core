package ru.chatan.data.database.chat

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.chatan.data.database.chat._enum.ChatUserRole
import ru.chatan.data.database.chat.models.ChatUserModel
import ru.chatan.data.database.dbQuery

class ChatUserService(private val database: Database) {

    object ChatUser : Table() {
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

    suspend fun fetchByUserId(userId: Long): List<ChatUserModel> {
        return dbQuery {
            ChatUser.select(where = { ChatUser.userId eq userId })
                .orderBy(ChatUser.id, SortOrder.ASC)
                .map {
                    ChatUserModel(
                        id = it[ChatUser.id],
                        userId = userId,
                        chatId = it[ChatUser.chatId],
                        role = ChatUserRole.valueOf(it[ChatUser.role])
                    )
                }
        }
    }

    suspend fun fetchByChatId(chatId: Long): List<ChatUserModel> {
        return dbQuery {
            ChatUser.select(where = { ChatUser.chatId eq chatId })
                .orderBy(ChatUser.id, SortOrder.ASC)
                .map {
                    ChatUserModel(
                        id = it[ChatUser.id],
                        userId = it[ChatUser.userId],
                        chatId = chatId,
                        role = ChatUserRole.valueOf(it[ChatUser.role])
                    )
                }
        }
    }

    suspend fun fetchByUserId(userId: Long, chatId: Long): ChatUserModel? {
        return dbQuery {
            val row =
                ChatUser.select(where = { (ChatUser.userId eq userId) and (ChatUser.chatId eq chatId) }).singleOrNull()
                    ?: return@dbQuery null

            ChatUserModel(
                id = row[ChatUser.id],
                userId = userId,
                chatId = chatId,
                role = ChatUserRole.valueOf(row[ChatUser.role])
            )
        }
    }

    suspend fun create(chatId: Long, userId: Long, role: ChatUserRole = ChatUserRole.USER) {
        dbQuery {
            ChatUser.insert {
                it[ChatUser.chatId] = chatId
                it[ChatUser.userId] = userId
                it[ChatUser.role] = role.name
            }
        }
    }

    suspend fun fetchUsersCount(chatId: Long): Long {
        return dbQuery {
            ChatUser.select(where = { ChatUser.chatId eq chatId }).count()
        }
    }

}