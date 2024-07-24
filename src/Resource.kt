package dev.schlaubi.telegram

import io.ktor.resources.*
import kotlinx.serialization.SerialName

@Resource("telegram")
class Telegram {
    @Resource("initiate")
    class Initiate(val parent: Telegram = Telegram())

    @Resource("callback")
    class Callback(
        val id: Int,
        @SerialName("first_name")
        val firstName: String? = null,
        @SerialName("last_name")
        val lastName: String? = null,
        val username: String,
        @SerialName("photo_url")
        val photoUrl: String? = null,
        val hash: String,
        @SerialName("auth_date")
        val authDate: Int,
        val parent: Telegram
    )
}

@Resource("oauth")
class OAuth {
    @Resource("authorize")
    data class Authorize(
        @SerialName("client_id")
        val clientId: String,
        @SerialName("redirect_uri")
        val redirectUri: String,
        @SerialName("response_type")
        val responseType: String,
        val scope: String,
        val state: String,
        val parent: OAuth
    )

    @Resource("token")
    data class Token(val parent: OAuth)

    @Resource("profile")
    data class Profile(val parent: OAuth)
}
