package de.fhac.ewi.routes

import de.fhac.ewi.services.TemperatureTimeSeriesService
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.temperature(service: TemperatureTimeSeriesService) {
    route("/temperature") {
        get("/keys") {
            call.respond(service.getAllKeys())
        }
        get("/series/{key}") {
            val key = call.parameters["key"] ?: throw IllegalArgumentException("No key provided.")
            call.respond(service.getSeries(key))
        }
    }
}