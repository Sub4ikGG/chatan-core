package ru.chatan.routing.chat.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRequest(
    val name: String,
    val description: String,
    val code: String,
    val userLimit: Int
)
