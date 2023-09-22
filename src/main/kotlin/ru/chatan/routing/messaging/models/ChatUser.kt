package ru.chatan.routing.messaging.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatUser(
    val id: Long,
    val name: String
)
