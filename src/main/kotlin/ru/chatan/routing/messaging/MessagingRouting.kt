package ru.chatan.routing.messaging

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import ru.chatan.Response
import ru.chatan.data.dao.SessionsRepository
import ru.chatan.data.database.chat.ChatService
import ru.chatan.data.database.chat.ChatUserService
import ru.chatan.data.database.chat.models.ChatModel
import ru.chatan.data.database.messaging.MessageService
import ru.chatan.data.database.user.UserService
import ru.chatan.data.database.user.models.UserModel
import ru.chatan.data.session.SessionsRepositoryImpl
import ru.chatan.data.session.models.Session
import ru.chatan.getDeviceId
import ru.chatan.getUserId
import ru.chatan.plugins.database
import ru.chatan.routing.messaging.models.*
import ru.chatan.service.JwtService

private const val CHAT_ID = "chatId"

fun Application.configureMessagingRouting() {
    val userService = UserService(database)

    val chatService = ChatService(database)
    val chatUserService = ChatUserService(database)
    val messageService = MessageService(database)

    val sessionsRepository = SessionsRepositoryImpl.newInstance()

    routing {
        get("/chat-messages") {
            getDeviceId() ?: return@get call.respond(Response.error<String>(code = 400))
            val userId = getUserId() ?: return@get call.respond(Response.error<Nothing>(code = 401))
            val chatId = call.request.queryParameters[CHAT_ID]?.toLongOrNull() ?: return@get call.respond(
                Response.error<String>(code = 400)
            )

            val chat = chatService.fetch(chatId = listOf(chatId)).singleOrNull()
                ?: return@get call.respond(Response.error<String>(code = 400))
            if (chatUserService.fetchByUserId(userId = userId, chatId = chat.id) == null) return@get call.respond(
                Response.error<String>(code = 400)
            )

            val messages = messageService.fetch(chatId = chat.id).map { messageModel ->
                val user =
                    if (messageModel.userId != -1L)
                        userService.fetch(userId = messageModel.userId) ?: return@get
                    else null

                val chatUser = if (user != null) ChatUser(id = user.id, name = user.name) else null

                ChatMessage(
                    id = messageModel.id,
                    user = chatUser,
                    body = messageModel.body
                )
            }

            call.respond(HttpStatusCode.OK, ChatMessages(messages = messages))
        }

        webSocket("/chat-messages") {
            val socketSession = this

            val accessJson = (incoming.receive() as? Frame.Text)?.readText()
            val access = Gson().fromJson(accessJson, ListenChatMessages::class.java)

            val userId =
                JwtService.decryptToken(token = access.token)?.toLongOrNull() ?: return@webSocket socketSession.close(
                    reason = CloseReason(code = 401, message = "Not authorized")
                )
            val user = userService.fetch(userId = userId) ?: return@webSocket socketSession.close(
                reason = CloseReason(
                    code = 404,
                    message = "User not found"
                )
            )
            val chatUser = ChatUser(id = user.id, name = user.name)

            val chat = chatService.fetch(chatId = listOf(access.chatId)).singleOrNull()
                ?: return@webSocket socketSession.close(reason = CloseReason(code = 404, message = "Chat not found"))
            if (chatUserService.fetchByUserId(
                    userId = userId,
                    chatId = chat.id
                ) == null
            ) return@webSocket socketSession.close(reason = CloseReason(code = 404, message = "User chat not found"))

            // Начало сессии
            val session = Session(
                userId = user.id,
                chatId = chat.id,
                session = socketSession
            )
            sessionsRepository.save(session = session)

            socketSession.listenWebSocketChatMessages(
                sessionsRepository = sessionsRepository,
                messageService = messageService,
                chat = chat,
                user = user,
                chatUser = chatUser
            )

            // Конец сессии
            sessionsRepository.remove(userId = user.id, chatId = chat.id)
        }
    }
}

private suspend fun DefaultWebSocketServerSession.listenWebSocketChatMessages(
    sessionsRepository: SessionsRepository,
    messageService: MessageService,
    chat: ChatModel,
    user: UserModel,
    chatUser: ChatUser
) {
    try {
        for (frame in this.incoming) {
            val frameJson = (frame as? Frame.Text)?.readText()
            val message = Gson().fromJson(frameJson, SendMessage::class.java)
            val chatMessageId =
                messageService.create(chatId = chat.id, userId = user.id, body = message.body)

            sessionsRepository.get(chatId = chat.id).forEach { userSession ->
                try {
                    if (userSession.userId != user.id) {
                        val chatMessage = ChatMessage(
                            id = chatMessageId,
                            user = chatUser,
                            body = message.body
                        )

                        userSession.session.send(Gson().toJson(chatMessage))
                    }
                } catch (e: Exception) {
                    userSession.session.close()
                    sessionsRepository.remove(userId = userSession.userId, chatId = userSession.chatId)
                }
            }
        }
    } catch (e: Exception) {
        sessionsRepository.remove(userId = user.id, chatId = chat.id)
        this.close(reason = CloseReason(code = 500, message = e.localizedMessage))

        println("Error with userId ${user.id} in chatId ${chat.id}: ${e.localizedMessage}")
        return
    }
}