package dev.schlaubi.telegram

import dev.schlaubi.telegram.route.oauth
import dev.schlaubi.telegram.route.telegram
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

val sessions = mutableMapOf<String, OAuth.Authorize>()

data class Session(val id: String) : Principal

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(Resources)
        install(ContentNegotiation) {
            json()
        }
        install(Sessions) {
            cookie<Session>("SESSION_ID")
        }
        install(Authentication) {
            session<Session>("session-auth") {
                validate { it.takeIf { dev.schlaubi.telegram.sessions.containsKey(it.id) } }
            }
        }
        routing {
            telegram()
            oauth()
        }
    }.start(wait = true)
}
