package ru.chatan.data.database.chat

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.chatan.data.database.chat._enum.ChatState
import ru.chatan.data.database.chat.models.ChatConfigurationModel
import ru.chatan.data.database.dbQuery
import java.time.ZoneId

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

    suspend fun fetch(chatId: Long): ChatConfigurationModel? {
        return dbQuery {
            val row = ChatConfiguration.select(where = { ChatConfiguration.chatId eq chatId }).singleOrNull() ?: return@dbQuery null

            ChatConfigurationModel(
                id = row[ChatConfiguration.id],
                chatId = chatId,
                userLimit = row[ChatConfiguration.userLimit],
                state = ChatState.valueOf(row[ChatConfiguration.state])
            )
        }
    }

    suspend fun create(chatId: Long, userLimit: Int, state: ChatState) {
        dbQuery {
            ChatConfiguration.insert {
                it[ChatConfiguration.chatId] = chatId
                it[ChatConfiguration.userLimit] = userLimit
                it[ChatConfiguration.state] = state.name
            }
        }
    }

}