package ru.chatan.routing.messaging.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessages(
    val messages: List<ChatMessage>
)