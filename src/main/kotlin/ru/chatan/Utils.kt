package ru.chatan

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import ru.chatan.Constants.DEVICE_ID

fun PipelineContext<Unit, ApplicationCall>.getDeviceId(): String? = this.call.request.headers[DEVICE_ID]