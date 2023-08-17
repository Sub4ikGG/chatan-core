package ru.chatan.data.database.chat.models

import ru.chatan.data.database.chat._enum.ChatState

@kotlinx.serialization.Serializable
data class ChatConfigurationModel(
    val id: Long,
    val chatId: Long,
    val userLimit: Int,
    val state: ChatState
)
