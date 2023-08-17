package ru.chatan.data.database.user.models

@kotlinx.serialization.Serializable
data class UserAvatarModel(
    val id: Long,
    val userId: Long,
    val uuid: String
)
