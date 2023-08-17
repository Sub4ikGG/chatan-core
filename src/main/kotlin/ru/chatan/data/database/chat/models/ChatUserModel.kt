package ru.chatan.data.database.chat.models

import ru.chatan.data.database.chat._enum.ChatUserRole

@kotlinx.serialization.Serializable
data class ChatUserModel(
    val id: Long,
    val userId: Long,
    val chatId: Long,
    val role: ChatUserRole
)