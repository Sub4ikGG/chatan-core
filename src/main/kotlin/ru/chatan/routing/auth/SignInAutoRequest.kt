package ru.chatan.routing.auth

@kotlinx.serialization.Serializable
data class SignInAutoRequest(
    val refreshToken: String
)
