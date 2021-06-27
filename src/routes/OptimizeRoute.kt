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
            val id = optimizerService.optimize(grid, optimizer)
            call.respond(optimizerService.getStatus(id))
        }
        route("{id}") {
            get {
                val id = call.parameters["id"]
                call.respond(optimizerService.getStatus(id))
            }
            get("/pipe/{pipe}") {
                val id = call.parameters["id"]
                val pipeId = call.parameters["pipe"]
                call.respond(optimizerService.getPipe(id, pipeId))
            }
            get("/node/{node}") {
                val id = call.parameters["id"]
                val nodeId = call.parameters["node"]
                call.respond(optimizerService.getNode(id, nodeId))
            }
            get("/overview") {
                val id = call.parameters["id"]
                call.respond(optimizerService.getOverview(id))

            }
        }
    }
}