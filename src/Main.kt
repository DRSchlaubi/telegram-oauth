package dev.schlaubi.telegram

import dev.kord.cache.api.data.description
import dev.kord.cache.api.delegate.DelegatingDataCache
import dev.kord.cache.api.query
import dev.kord.cache.map.MapLikeCollection
import dev.kord.cache.map.internal.MapEntryCache
import dev.kord.cache.redis.RedisConfiguration
import dev.kord.cache.redis.RedisEntryCache
import dev.schlaubi.telegram.route.oauth
import dev.schlaubi.telegram.route.telegram
import freemarker.cache.ClassTemplateLoader
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.lettuce.core.RedisClient
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
data class DataSession(val id: String, val redirectUri: String)
data class Session(val id: String)

@OptIn(ExperimentalSerializationApi::class)
private val redisConfig by lazy {
    RedisConfiguration {
        client = RedisClient.create(Config.REDIS_URL)
        binaryFormat = ProtoBuf {
            encodeDefaults = false
        }
        reuseConnection = true
    }
}

val cache = DelegatingDataCache {
    default { cache, description ->
        if (Config.REDIS_URL != null) {
            RedisEntryCache(cache, description, redisConfig)
        } else {
            MapEntryCache(cache, description, MapLikeCollection.concurrentHashMap())
        }
    }
}

suspend fun main() {
    cache.register(description(DataSession::id))

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
                validate { it.takeIf { cache.query<DataSession> { DataSession::id eq it.id }.singleOrNull() != null } }
            }
        }
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(ClassLoader.getSystemClassLoader(), "templates")
        }
        routing {
            telegram()
            oauth()
        }
    }.start(wait = true)
}
