package ru.chatan.routing.chat.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatResponse(
    val chatId: Long
)
