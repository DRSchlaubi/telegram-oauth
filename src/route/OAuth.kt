package dev.schlaubi.telegram.route

import com.auth0.jwt.exceptions.JWTVerificationException
import dev.schlaubi.telegram.*
import dev.schlaubi.telegram.models.TokenResponse
import dev.schlaubi.telegram.models.UserResponse
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.hours

fun Route.oauth() {
    get<OAuth.Authorize> {
        sessions[it.state] = it
        call.sessions.set(Session(it.state))

        if (it.responseType != "code") {
            throw BadRequestException("Only code is supported as response_type")
        }
        if (it.redirectUri !in Config.OAUTH_REDIRECT_URIS) {
            throw BadRequestException("Invalid redirect URI")
        }
        if (it.clientId !in Config.OAUTH_CLIENT_ID) {
            throw BadRequestException("Invalid client id")
        }
        call.respondRedirect(application.href(Telegram.Initiate()))
    }

    post<OAuth.Token> {
        val formData = call.receiveParameters()

        val code = formData["code"] ?: throw BadRequestException("Missing code")
        if (formData["redirect_uri"] !in Config.OAUTH_REDIRECT_URIS) {
            throw BadRequestException("Invalid redirect URI")
        }
        if (formData["client_id"] != Config.OAUTH_CLIENT_ID) {
            throw BadRequestException("Invalid client id")
        }
        if (formData["client_secret"] != Config.OAUTH_CLIENT_SECRET) {
            throw BadRequestException("Invalid client id")
        }
        if (formData["grant_type"] != "authorization_code") {
            throw BadRequestException("Only authorization_code is supported as grant_type")
        }
        val user = try {
            val data = authKeyVerifier.verify(code).claims["user_data"]!!.asString()
            Json.decodeFromString<UserResponse>(data)
        } catch (e: JWTVerificationException) {
            e.printStackTrace()
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }

        val fullKey = newAccessToken(user)
        call.respond(
            TokenResponse(
                fullKey,
                "Bearer",
                24.hours.inWholeSeconds
            )
        )
    }

    get<OAuth.Profile> {
        val token = (call.request.parseAuthorizationHeader() as? HttpAuthHeader.Single)?.blob
            ?: throw BadRequestException("Missing auth")
        val user = try {
            val data = accessTokenVerifier.verify(token).claims["user_data"]!!.asString()
            Json.decodeFromString<UserResponse>(data)
        } catch (e: JWTVerificationException) {
            e.printStackTrace()
            call.respond(HttpStatusCode.Unauthorized)
            return@get
        }

        call.respond(user)
    }
}
