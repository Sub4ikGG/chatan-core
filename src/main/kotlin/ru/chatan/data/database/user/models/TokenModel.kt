package ru.chatan.data.database.user.models

@kotlinx.serialization.Serializable
data class TokenModel(
    val id: Long,
    val userId: Long,
    val deviceId: String,
    val refreshToken: String
)
