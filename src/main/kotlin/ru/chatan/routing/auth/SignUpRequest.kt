package ru.chatan.routing.auth

@kotlinx.serialization.Serializable
data class SignUpRequest(
    val name: String,
    val password: String
)