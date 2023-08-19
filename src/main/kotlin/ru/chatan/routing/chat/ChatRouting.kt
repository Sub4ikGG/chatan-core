package ru.chatan.routing.chat

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.chatan.Response
import ru.chatan.data.database.chat.ChatService
import ru.chatan.data.database.chat.ChatUserService
import ru.chatan.getDeviceId
import ru.chatan.getUserId
import ru.chatan.plugins.database
import ru.chatan.routing.chat.models.ChatsResponse

fun Application.configureChatRouting() {
    val chatService = ChatService(database)
    val chatUserService = ChatUserService(database)

    routing {
        get("/chats") {
            getDeviceId() ?: return@get call.respond(Response.error<String>(code = 400))
            val userId = getUserId() ?: return@get call.respond(Response.error<Nothing>(code = 401))

            val userChats = chatUserService.fetch(userId = userId)
            val userChatsId = userChats.map { it.chatId }
            val chats = chatService.fetch(chatId = userChatsId).map {
                ChatsResponse(
                    chatId = it.id,
                    name = it.name
                )
            }

            call.respond(HttpStatusCode.OK, Response.success(data = chats))
        }
    }
}