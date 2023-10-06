package ru.chatan.data.database.messaging

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.chatan.data.database.dbQuery
import ru.chatan.data.database.messaging.models.MessageModel

class MessageService(private val database: Database) {

    object Message : Table() {
        val id = long("id").autoIncrement()
        val chatId = long("chat_id")
        val userId = long("user_id")
        val body = varchar("body", 4096)
        val date = long("date")
    }

    init {
        transaction(database) {
            SchemaUtils.create(Message)
        }
    }

    suspend fun create(chatId: Long, userId: Long, body: String, date: Long): Long {
        return dbQuery {
            Message.insert {
                it[Message.chatId] = chatId
                it[Message.userId] = userId
                it[Message.body] = body.take(4096)
                it[Message.date] = date
            }[Message.id]
        }
    }

    suspend fun fetch(chatId: Long, page: Int): List<MessageModel> {
        return dbQuery {
            Message.select(where = { Message.chatId eq chatId })
                .orderBy(Message.id, SortOrder.DESC)
                .limit(n = PAGE_SIZE, offset = (page * PAGE_SIZE).toLong())
                .map {
                    MessageModel(
                        id = it[Message.id],
                        chatId = chatId,
                        userId = it[Message.userId],
                        body = it[Message.body],
                        date = it[Message.date]
                    )
                }
        }
    }

    private companion object {
        const val PAGE_SIZE = 200
    }

}
