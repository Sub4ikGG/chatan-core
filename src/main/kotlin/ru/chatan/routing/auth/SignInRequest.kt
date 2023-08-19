package ru.chatan.routing.auth

@kotlinx.serialization.Serializable
data class SignInRequest(
    val name: String,
    val password: String
)
