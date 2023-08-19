package ru.chatan.routing.chat.models

@kotlinx.serialization.Serializable
data class ChatsResponse(
    val chatId: Long,
    val name: String
)