package ru.chatan.routing.messaging.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: Long,
    val user: ChatUser?,
    val body: String,
    val date: Long
)