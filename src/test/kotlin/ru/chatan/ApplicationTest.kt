package ru.chatan

import io.ktor.server.testing.*
import ru.chatan.plugins.configureRouting
import kotlin.test.Test

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
//        client.get("/").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals(true, bodyAsText().isNotBlank())
//        }
    }
}










