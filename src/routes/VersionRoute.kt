package de.fhac.ewi.routes

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.response.*
import io.ktor.routing.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Route.version(config: ApplicationConfig) {
    val version = config.propertyOrNull("version")?.getString() ?: "version property not found in application.conf"
    val compileTimestamp = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(config.propertyOrNull("timestamp")?.getString()?.toLong() ?: 0),
        ZoneId.systemDefault()
    ).toString()
    val startupTimestamp = LocalDateTime.now().toString()

    get("/version") {
        call.respond(
            mapOf(
                "version" to version,
                "compiled" to compileTimestamp,
                "startup" to startupTimestamp,
                "current" to LocalDateTime.now().toString()
            )
        )
    }
}