package de.fhac.ewi.routes

import de.fhac.ewi.dto.GridRequest
import de.fhac.ewi.services.GridService
import de.fhac.ewi.util.catchAndThrowIllegalArgument
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.grid(gridService: GridService) {
    route("/grid") {
        post("/verify") {
            val request = call.receive<GridRequest>()
            catchAndThrowIllegalArgument { gridService.createByGridRequest(request) }
            call.respond(HttpStatusCode.OK)
        }

        post("/validate") {
            val request = call.receive<GridRequest>()
            val grid = catchAndThrowIllegalArgument { gridService.createByGridRequest(request) }
            grid.validate()
            call.respond(HttpStatusCode.OK)
        }

        post("/maxmassenstrom") {
            val request = call.receive<GridRequest>()
            val grid = catchAndThrowIllegalArgument { gridService.createByGridRequest(request) }
            grid.validate()
            call.respond(gridService.calculateMaxMassenstrom(grid, request.temperatureSeries))
        }
    }
}