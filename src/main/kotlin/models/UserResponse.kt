package dev.schlaubi.telegram.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val sub: String,
    val name: String,
    @SerialName("given_name")
    val givenName: String?,
    @SerialName("family_name")
    val familyName: String?,
    val picture: String?
)
