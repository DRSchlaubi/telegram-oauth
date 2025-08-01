package dev.schlaubi.telegram.route

import dev.kord.cache.api.query
import dev.schlaubi.telegram.*
import dev.schlaubi.telegram.models.UserResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

fun Route.telegram() {
    authenticate("session-auth") {
        get<Telegram.Initiate> {
            call.respond(
                FreeMarkerContent(
                    "index.ftl",
                    mapOf("config" to Config)
                )
            )
        }

        get<Telegram.Callback> {
            // https://core.telegram.org/widgets/login
            val dataCheckString = call.parameters
                .filter { key, _ -> key != "hash" }
                .entries()
                // "sorted in alphabetical order"
                .sortedBy(Map.Entry<String, *>::key)
                // "in the format key=<value> with a line feed character"
                .joinToString("\n") { (key, value) -> "$key=${value.first()}" }

            if (hmacSHA256(dataCheckString, Config.TELEGRAM_TOKEN) != it.hash) {
                return@get call.respond(HttpStatusCode.Unauthorized)
            }
            // https://gist.github.com/anonymous/6516521b1fb3b464534fbc30ea3573c2#file-check_authorization-php-L19-L21
            if (System.currentTimeMillis() / 1000 - it.authDate > 86400) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }
            val user = UserResponse(
                it.id.toString(),
                it.username,
                it.firstName,
                it.lastName,
                it.photoUrl
            )

            val request = call.principal<Session>()!!
            val data = cache.query<DataSession> {
                DataSession::id eq request.id
            }
            val (state, redirectUri) = data.singleOrNull() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            data.remove()

            val responseUri = URLBuilder(redirectUri).apply {
                parameters["code"] = newAuthCode(user)
                parameters["state"] = state
            }.build()
            call.respondRedirect(responseUri)
        }
    }
}
