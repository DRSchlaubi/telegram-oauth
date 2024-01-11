plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

group = "dev.schlaubi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:2.3.7"))
    implementation("io.ktor", "ktor-server-netty")
    implementation("io.ktor", "ktor-server-content-negotiation")
    implementation("io.ktor", "ktor-serialization-kotlinx-json")
    implementation("io.ktor", "ktor-server-sessions")
    implementation("io.ktor", "ktor-server-resources")
    implementation("io.ktor", "ktor-server-auth")
    implementation("org.slf4j", "slf4j-simple", "2.0.7")
    implementation("dev.schlaubi", "stdx-full", "1.3.0")
    implementation("com.auth0", "java-jwt", "4.4.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "dev.schlaubi.telegram.MainKt"
}