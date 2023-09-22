package ru.chatan.routing.messaging.models

import kotlinx.serialization.Serializable

@Serializable
data class SendMessage(
    val body: String
)
