package ru.chatan.routing.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.chatan.Response
import ru.chatan.data.database.user.UserService
import ru.chatan.data.database.user.UserTokenService
import ru.chatan.getDeviceId
import ru.chatan.plugins.database
import ru.chatan.service.JwtService
import ru.chatan.service.PasswordService

fun Application.configureAuthRouting() {
    val userService = UserService(database)
    val userTokenService = UserTokenService(database)

    routing {
        post("/sign-up") {
            val deviceId = getDeviceId() ?: return@post call.respond(Response.error<String>(code = 400))
            val signUpRequest = call.receive<SignUpRequest>()
            if (userService.fetch(name = signUpRequest.name) != null) return@post call.respond(Response.error<String>(code = 409))

            val securedPassword = PasswordService.encryptPassword(password = signUpRequest.password)
            val userId = userService.create(name = signUpRequest.name, password = securedPassword)

            val token = JwtService.encryptJson(json = userId.toString())
            val refreshToken = JwtService.generateRefreshToken()

            userTokenService.create(userId = userId, deviceId = deviceId, refreshToken = refreshToken)

            call.respond(HttpStatusCode.OK, Response.success(data = SignUpResponse(token = token, refreshToken = refreshToken)))
        }
    }
}