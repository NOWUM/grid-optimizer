package de.fhac.ewi.routes

import de.fhac.ewi.dto.GridRequest
import de.fhac.ewi.services.GridService
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Route.grid(gridService: GridService) {
    route("/grid") {
        post("/validate") {
            val request = call.receive<GridRequest>()
            val grid = gridService.create(request.nodes, request.pipes)
            call.respond(grid)
        }
    }
}