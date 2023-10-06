package ru.chatan.data.database.messaging.models

import kotlinx.serialization.Serializable

@Serializable
data class MessageModel(
    val id: Long,
    val chatId: Long,
    val userId: Long,
    val body: String,
    val date: Long
)
