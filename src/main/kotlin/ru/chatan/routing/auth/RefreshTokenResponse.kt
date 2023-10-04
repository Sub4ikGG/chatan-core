package ru.chatan.routing.auth

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    val token: String,
    val refreshToken: String,
)