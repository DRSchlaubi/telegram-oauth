package dev.schlaubi.telegram

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Verification
import dev.schlaubi.telegram.models.UserResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun newAccessToken(telegramUser: UserResponse): String =
    newKey("access_token", telegramUser.sub, telegramUser, 24.hours)

fun newAuthCode(telegramUser: UserResponse): String =
    newKey("auth_code", telegramUser.sub, telegramUser, 5.minutes)

val authKeyVerifier = verifier {
    withClaim("type", "auth_code")
}

val accessTokenVerifier = verifier {
    withClaim("type", "access_token")
}

private fun verifier(builder: Verification.() -> Unit) = JWT
    .require(Algorithm.HMAC256(Config.JWT_SECRET))
    .apply(builder)
    .build()

private fun newKey(type: String, id: String, telegramUser: UserResponse, expiresIn: Duration) = JWT.create()
    .withClaim("userId", id)
    .withClaim("type", type)
    .withClaim("user_data", Json.encodeToString(telegramUser))
    .withExpiresAt(Instant.ofEpochMilli(System.currentTimeMillis() + expiresIn.inWholeMilliseconds))
    .sign(Algorithm.HMAC256(Config.JWT_SECRET))
