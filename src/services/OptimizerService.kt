package de.fhac.ewi.services

import de.fhac.ewi.dto.OptimizationRequest
import de.fhac.ewi.dto.OptimizationResponse
import de.fhac.ewi.dto.OptimizedNodeResponse
import de.fhac.ewi.dto.OptimizedPipeResponse
import de.fhac.ewi.model.Grid
import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.PipeType
import de.fhac.ewi.util.catchParseError
import de.fhac.ewi.util.toDoubleFunction

class OptimizerService {

    fun createByOptimizationRequest(request: OptimizationRequest): Optimizer {
        val pipeTypes =
            request.pipeTypes.map {
                catchParseError("Could not parse PipeType $it.") {
                    PipeType(
                        it.diameter / 1000.0,
                        it.costPerMeter
                    )
                }
            }
        val pipeOperationCostFunc =
            catchParseError("Invalid cost function for grid operation cost.") { request.gridOperatingCostTemplate.toDoubleFunction() }
        val pumpInvestCostFunc =
            catchParseError("Invalid cost function for pump invest cost") { request.pumpInvestCostTemplate.toDoubleFunction() }

        return Optimizer(
            pipeTypes,
            pipeOperationCostFunc,
            pumpInvestCostFunc,
            request.heatGenerationCost,
            request.lifespanOfGrid,
            request.lifespanOfPump,
            request.yearsOfOperation,
            request.wacc,
            request.electricityCost,
            request.electricalEfficiency,
            request.hydraulicEfficiency
        )
    }


    fun optimize(grid: Grid, optimizer: Optimizer): OptimizationResponse {
        optimizer.optimize(grid)
        return OptimizationResponse(
            optimizer.calculateCosts(grid),
            grid.pipes.map { OptimizedPipeResponse(it.id, it.type.diameter) },
            grid.nodes.map { OptimizedNodeResponse(it.id, it.connectedThermalEnergyDemand.curve) }
        )
    }
}