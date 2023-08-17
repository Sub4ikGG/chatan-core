package ru.chatan.data.database.user.models

@kotlinx.serialization.Serializable
data class UserModel(
    val id: Long,
    val name: String,
    val password: String
)