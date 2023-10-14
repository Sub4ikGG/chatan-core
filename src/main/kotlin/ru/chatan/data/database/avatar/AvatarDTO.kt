package ru.chatan.data.database.avatar

import kotlinx.serialization.Serializable
import ru.chatan.fileStorage

@Serializable
data class AvatarDTO(
    val id: Long,
    val userId: Long,
    val uuid: String
) {
    fun toUserAvatar() =
        UserAvatar(
            href = fileStorage.buildHref(uuid = uuid, type = "jpg")
        )
}
