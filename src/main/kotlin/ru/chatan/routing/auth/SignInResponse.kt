package ru.chatan.routing.auth

@kotlinx.serialization.Serializable
data class SignInResponse(
    val name: String,
    val token: String,
    val refreshToken: String
)
