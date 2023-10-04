package ru.chatan

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.chatan.plugins.*
import ru.chatan.routing.auth.configureAuthRouting
import ru.chatan.routing.chat.configureChatRouting
import ru.chatan.routing.messaging.configureMessagingRouting

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureHTTP()

    // Routing
    configureRouting()
    configureAuthRouting()
    configureChatRouting()
    configureMessagingRouting()
}