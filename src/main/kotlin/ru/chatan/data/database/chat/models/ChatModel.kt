package ru.chatan.data.database.chat.models

@kotlinx.serialization.Serializable
data class ChatModel(
    val id: Long,
    val code: String,
    val name: String,
    val description: String,
)
