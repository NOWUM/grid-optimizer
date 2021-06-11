package de.fhac.ewi.services

import de.fhac.ewi.dto.OptimizationRequest
import de.fhac.ewi.dto.OptimizationResponse
import de.fhac.ewi.dto.OptimizedNodeResponse
import de.fhac.ewi.dto.OptimizedPipeResponse
import de.fhac.ewi.model.Grid
import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.PipeType
import de.fhac.ewi.util.catchAndThrowIllegalArgument
import de.fhac.ewi.util.toDoubleFunction

class OptimizerService {

    fun createByOptimizationRequest(request: OptimizationRequest): Optimizer {
        val pipeTypes =
            catchAndThrowIllegalArgument { request.pipeTypes.map { PipeType(it.diameter / 1000.0, it.costPerMeter) } }
        val pipeOperationCostFunc =
            catchAndThrowIllegalArgument { request.gridOperatingCostTemplate.toDoubleFunction() }
        val pumpInvestCostFunc = catchAndThrowIllegalArgument { request.pumpInvestCostTemplate.toDoubleFunction() }

        return Optimizer(
            pipeTypes,
            pipeOperationCostFunc,
            pumpInvestCostFunc,
            request.heatGenerationCost,
            request.lifespanOfGrid,
            request.lifespanOfPump,
            request.yearsOfOperation,
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
            grid.nodes.map { OptimizedNodeResponse(it.id) }
        )
    }
}