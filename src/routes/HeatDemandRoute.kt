package de.fhac.ewi.routes

import de.fhac.ewi.dto.HeatDemandRequest
import de.fhac.ewi.services.HeatDemandService
import de.fhac.ewi.services.TemperatureTimeSeriesService
import de.fhac.ewi.util.repeatEach
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.heatDemand(service: HeatDemandService, tempService: TemperatureTimeSeriesService) {
    route("/heatdemand") {
        post {
            val request = call.receive<HeatDemandRequest>()
            val profile = service.retrieveProfile(request.loadProfileName, request.temperatureSeries)
            val temperature = tempService.getSeries(request.temperatureSeries)
            call.respond(
                mapOf(
                    "profile" to profile,
                    "curve" to profile.createHeatDemandCurve(request.thermalEnergyDemand).curve,
                    "temperature" to temperature.temperatures.repeatEach(24),
                    "allokation" to profile.allokation.repeatEach(24),
                    "dailyHeatCurve" to profile.dailyHeatCurve.repeatEach(24)
                )
            )
        }
    }
}