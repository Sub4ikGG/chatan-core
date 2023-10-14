package ru.chatan.data.database.avatar

import kotlinx.serialization.Serializable
import ru.chatan.fileStorage

@Serializable
data class UserAvatar(
    val href: String
) {
    companion object {
        fun noAvatar() = UserAvatar(
            href = fileStorage.buildHref("no-avatar", "jpg")
        )
    }
}
