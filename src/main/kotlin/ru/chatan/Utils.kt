package ru.chatan

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import ru.chatan.Constants.DEVICE_ID
import ru.chatan.Constants.TOKEN
import ru.chatan.service.JwtService

fun PipelineContext<Unit, ApplicationCall>.getDeviceId(): String? = this.call.request.headers[DEVICE_ID]

fun PipelineContext<Unit, ApplicationCall>.getUserId(): Long? = JwtService.decryptToken(this.call.request.headers[TOKEN].toString())?.toLongOrNull()