package de.fhac.ewi.routes

import de.fhac.ewi.dto.HeatDemandRequest
import de.fhac.ewi.services.HeatDemandService
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.heatDemand(service: HeatDemandService) {
    route("/heatdemand") {
        post {
            val request = call.receive<HeatDemandRequest>()
            val profile = service.retrieveProfile(request.loadProfileName, request.temperatureSeries)

            call.respond(
                mapOf(
                    "profile" to profile,
                    "curve" to profile.createHeatDemandCurve(request.thermalEnergyDemand).curve
                )
            )
        }
    }
}