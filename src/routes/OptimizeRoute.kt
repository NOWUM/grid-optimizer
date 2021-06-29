package de.fhac.ewi.routes

import de.fhac.ewi.dto.OptimizationRequest
import de.fhac.ewi.services.GridService
import de.fhac.ewi.services.OptimizerService
import de.fhac.ewi.util.catchParseError
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.optimize(gridService: GridService, optimizerService: OptimizerService) {
    route("optimize") {
        post {
            val request = call.receive<OptimizationRequest>()
            val grid = catchParseError { gridService.createByGridRequest(request.grid) }
            grid.validate()
            val optimizer = catchParseError { optimizerService.createByOptimizationRequest(request) }
            call.respond(optimizerService.optimize(grid, optimizer))
        }
    }
}