package dev.schlaubi.telegram

import dev.schlaubi.envconf.Config

object Config : Config() {
    val TELEGRAM_TOKEN by getEnv(transform = String::hashBinarySha256)
    val TELEGRAM_BOT by this
    // You can: pwgen -s 50 1
    val JWT_SECRET by getEnv("verrysecurenonsense")
    val URL by getEnv("http://localhost:8080")

    val OAUTH_REDIRECT_URIS by getEnv { it.split(",\\s*".toRegex()) }
    val OAUTH_CLIENT_ID by this
    val OAUTH_CLIENT_SECRET by this

    val REDIS_URL by getEnv().optional()
}
