package ru.chatan.routing.file

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.chatan.Response
import ru.chatan.data.database.avatar.AvatarService
import ru.chatan.data.database.avatar.UserAvatar
import ru.chatan.getUserId
import ru.chatan.plugins.database
import ru.chatan.routing.file.models.UploadAvatar
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun Application.configureFileRouting() {
    val avatarService = AvatarService(database = database)

    routing {
        host("api.chatan.ru") {

            post("/avatar/upload") {
                val userId = getUserId() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val uploadAvatar = call.receive<UploadAvatar>()

                val uuid = UUID.nameUUIDFromBytes(System.currentTimeMillis().toString().toByteArray()).toString()
                val avatarBytes = Base64.getDecoder().decode(uploadAvatar.base64)
                val file = File("$uuid.jpeg")

                withContext(Dispatchers.IO) {
                    file.createNewFile()

                    val fos = FileOutputStream(file)
                    fos.write(avatarBytes)
                    fos.flush()
                    fos.close()
                }

                avatarService.create(userId = userId, uuid = uuid)
                val avatar = avatarService.fetch(userId = userId) ?: UserAvatar.noAvatar()
                call.respond(Response.success(data = avatar))
            }

        }
    }
}