package ru.chatan.routing.messaging.models

import kotlinx.serialization.Serializable

@Serializable
data class ListenChatMessages(
    val deviceId: Long,
    val token: String,
    val chatId: Long
)