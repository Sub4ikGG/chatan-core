package ru.chatan.routing.chat

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import ru.chatan.Response
import ru.chatan.data.database.chat.ChatConfigurationService
import ru.chatan.data.database.chat.ChatService
import ru.chatan.data.database.chat.ChatUserService
import ru.chatan.data.database.chat._enum.ChatState
import ru.chatan.data.database.chat._enum.ChatUserRole
import ru.chatan.data.database.chat.models.ChatModel
import ru.chatan.data.database.messaging.MessageService
import ru.chatan.data.database.user.UserService
import ru.chatan.data.database.user.models.UserModel
import ru.chatan.data.session.SessionsRepositoryImpl
import ru.chatan.getDeviceId
import ru.chatan.getUserId
import ru.chatan.plugins.database
import ru.chatan.routing.chat.models.ChatsResponse
import ru.chatan.routing.chat.models.ConnectToTheChatRequest
import ru.chatan.routing.chat.models.CreateChatRequest
import ru.chatan.routing.chat.models.CreateChatResponse
import ru.chatan.routing.messaging.models.ChatMessage

private const val CHAT_ID = "chatId"

fun Application.configureChatRouting() {
    val chatService = ChatService(database)
    val chatUserService = ChatUserService(database)
    val chatConfigurationService = ChatConfigurationService(database)

    val userService = UserService(database)
    val messageService = MessageService(database)
    val sessionsRepository = SessionsRepositoryImpl.newInstance()

    launch {
        val chatId = chatService.create(name = "Chatan", description = "Chatan", code = "#chatan")
        chatUserService.create(chatId = chatId, userId = 1, role = ChatUserRole.SUPERUSER)
        chatConfigurationService.create(chatId = chatId, userLimit = 10, state = ChatState.OPENED)
    }

    routing {
        get("/chats") {
            getDeviceId() ?: return@get call.respond(Response.error<String>(code = 400))
            val userId = getUserId() ?: return@get call.respond(Response.error<Nothing>(code = 401))

            val userChats = chatUserService.fetchByUserId(userId = userId)
            val userChatsId = userChats.map { it.chatId }
            val chats = chatService.fetch(chatId = userChatsId).map {
                ChatsResponse(
                    chatId = it.id,
                    name = it.name
                )
            }

            call.respond(HttpStatusCode.OK, Response.success(data = chats))
        }

        post("/create-chat") {
            getDeviceId() ?: return@post call.respond(Response.error<String>(code = 400))
            val userId = getUserId() ?: return@post call.respond(Response.error<Nothing>(code = 401))
            val createChatRequest = call.receive<CreateChatRequest>()

            if (createChatRequest.userLimit < 1) return@post call.respond(Response.error<Nothing>(code = 400))
            if (createChatRequest.code.first() != '#') return@post call.respond(Response.error<Nothing>(code = 400))
            if (createChatRequest.code.replace("#", " ").length < 3) return@post call.respond(Response.error<Nothing>(code = 400))

            val chatId = chatService.create(name = createChatRequest.name, description = createChatRequest.description, code = createChatRequest.code)
            chatUserService.create(chatId = chatId, userId = userId, role = ChatUserRole.SUPERUSER)
            chatConfigurationService.create(chatId = chatId, userLimit = createChatRequest.userLimit, state = ChatState.OPENED)

            call.respond(HttpStatusCode.OK, Response.success(data = CreateChatResponse(chatId = chatId)))
        }

        post("/connect-to-the-chat") {
            getDeviceId() ?: return@post call.respond(Response.error<String>(code = 400))
            val userId = getUserId() ?: return@post call.respond(Response.error<Nothing>(code = 401))
            val connectToTheChatRequest = call.receive<ConnectToTheChatRequest>()

            val user = userService.fetch(userId = userId) ?: return@post call.respond(
                Response.error<Nothing>(
                    code = 400,
                    message = "Пользователь не найден"
                )
            )
            val chat = chatService.fetch(chatId = listOf(connectToTheChatRequest.chatId)).singleOrNull() ?: return@post call.respond(Response.error<Nothing>(code = 404, message = "Чат не найден"))
            val chatConfiguration = chatConfigurationService.fetch(chatId = connectToTheChatRequest.chatId) ?: return@post call.respond(Response.error<Nothing>(code = 404, message = "Конфигурация чата не найдена"))
            val chatUsersCount = chatUserService.fetchUsersCount(chatId = chat.id)

            if (chatUserService.fetchByUserId(chatId = chat.id, userId = userId) != null) return@post call.respond(Response.error<Nothing>(code = 400))
            if (chat.code != connectToTheChatRequest.code) return@post call.respond(Response.error<Nothing>(code = 400, message = "Неверный код доступа"))
            if (chatConfiguration.userLimit < chatUsersCount + 1)
                return@post call.respond(Response.error<Nothing>(code = 400, message = "Лимит участников чата превышен"))

            val message = getWelcomeChatMessage(user, messageService, chat)
            sessionsRepository.get(chatId = chat.id).forEach { userSession ->
                try {
                    userSession.session.send(Gson().toJson(message))
                } catch (e: Exception) {
                    sessionsRepository.remove(userId = userSession.userId, chatId = userSession.chatId)
                    userSession.session.close()
                }
            }

            chatUserService.create(chatId = chat.id, userId = userId)
            call.respond(HttpStatusCode.OK, Response.success<Nothing>())
        }

        get("/get-chat-users") {
            getDeviceId() ?: return@get call.respond(Response.error<String>(code = 400))
            val userId = getUserId() ?: return@get call.respond(Response.error<Nothing>(code = 401))
            val chatId = call.request.queryParameters[CHAT_ID]?.toLongOrNull() ?: return@get call.respond(Response.error<Nothing>(code = 400))

            val chat = chatUserService.fetchByUserId(chatId = chatId, userId = userId) ?: return@get call.respond(Response.error<Nothing>(code = 404, message = "Чат не найден"))
            val chatUsers = chatUserService.fetchByChatId(chatId = chat.id)
            call.respond(HttpStatusCode.OK, Response.success(data = chatUsers))
        }
    }
}

private suspend fun getWelcomeChatMessage(
    user: UserModel,
    messageService: MessageService,
    chat: ChatModel
): ChatMessage {
    val body = "${user.name} присоединился в чат!"
    val chatMessageId =
        messageService.create(chatId = chat.id, userId = -1, body = body)

    return ChatMessage(
        id = chatMessageId,
        user = null,
        body = body
    )
}