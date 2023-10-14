package ru.chatan.routing.file.models

import kotlinx.serialization.Serializable

@Serializable
data class UploadAvatar(
    val base64: String
)
