package ru.chatan.routing.chat.models

import kotlinx.serialization.Serializable

@Serializable
data class ConnectToTheChatRequest(
    val code: String
)