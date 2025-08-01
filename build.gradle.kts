plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass = "dev.schlaubi.telegram.MainKt"
}

dependencies {
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.freemarker)
    implementation(libs.ktor.server.auth)
    implementation(libs.slf4j.simple)
    implementation(libs.stdx.full)
    implementation(libs.java.jwt)
    implementation(libs.kord.cache.api)
    implementation(libs.kord.cache.map)
    implementation(libs.kord.cache.redis)
}