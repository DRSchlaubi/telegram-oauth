package dev.schlaubi.telegram

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@OptIn(ExperimentalStdlibApi::class)
fun hmacSHA256(message: String, secret: ByteArray): String {
    val mac = Mac.getInstance("HmacSHA256")
    val secretKeySpec = SecretKeySpec(secret, "HmacSHA256")
    mac.init(secretKeySpec)
    val hash = mac.doFinal(message.toByteArray())

    return hash.toHexString()
}

fun String.hashBinarySha256(): ByteArray {
    return MessageDigest
        .getInstance("SHA-256")
        .digest(toByteArray())
}

