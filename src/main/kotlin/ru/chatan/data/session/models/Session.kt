package ru.chatan.data.session.models

import io.ktor.server.websocket.*

data class Session(
    val userId: Long,
    val chatId: Long,
    val session: DefaultWebSocketServerSession
)
