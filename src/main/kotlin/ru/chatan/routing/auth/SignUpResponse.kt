package ru.chatan.routing.auth

@kotlinx.serialization.Serializable
data class SignUpResponse(
    val name: String,
    val token: String,
    val refreshToken: String
)
