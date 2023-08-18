package ru.chatan.routing.auth

@kotlinx.serialization.Serializable
data class SignUpResponse(
    val token: String,
    val refreshToken: String
)
