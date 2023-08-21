package ru.chatan.routing.chat.models

import kotlinx.serialization.Serializable
import java.io.Serial

@Serializable
data class ConnectToTheChatRequest(
    val chatId: Long,
    val code: String
)