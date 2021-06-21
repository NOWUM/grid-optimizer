package de.fhac.ewi.services

import de.fhac.ewi.dto.OptimizationRequest
import de.fhac.ewi.dto.OptimizationResponse
import de.fhac.ewi.dto.OptimizedNodeResponse
import de.fhac.ewi.dto.OptimizedPipeResponse
import de.fhac.ewi.model.Grid
import de.fhac.ewi.model.InvestmentParameter
import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.PipeType
import de.fhac.ewi.util.catchParseError
import de.fhac.ewi.util.toDoubleFunction

class OptimizerService {

    fun createByOptimizationRequest(request: OptimizationRequest): InvestmentParameter {
        val pipeTypes =
            request.pipeTypes.map {
                catchParseError("Could not parse PipeType $it.") {
                    PipeType(
                        it.diameter / 1000.0,
                        it.costPerMeter,
                        it.isolationThickness / 1000.0,
                        it.distanceBetweenPipes / 1000.0
                    )
                }
            }
        val pipeOperationCostFunc =
            catchParseError("Invalid cost function for grid operation cost.") { request.gridOperatingCostTemplate.toDoubleFunction() }
        val pumpInvestCostFunc =
            catchParseError("Invalid cost function for pump invest cost") { request.pumpInvestCostTemplate.toDoubleFunction() }

        return InvestmentParameter(
            pipeTypes,
            pipeOperationCostFunc,
            pumpInvestCostFunc,
            request.heatGenerationCost,
            request.lifespanOfGrid,
            request.lifespanOfPump,
            request.wacc,
            request.electricityCost,
            request.electricalEfficiency,
            request.hydraulicEfficiency
        )
    }


    fun optimize(grid: Grid, investmentParameter: InvestmentParameter): OptimizationResponse {
        val optimizer = Optimizer(grid, investmentParameter)
        optimizer.optimize()
        println("> Checked ${optimizer.numberOfTypeChecks} pipe types and made ${optimizer.numberOfUpdates} for perfect grid. (grid had ${grid.pipes.size} pipes)")
        println("> Grid costs now ${optimizer.gridCosts}")
        return OptimizationResponse(
            optimizer.gridCosts,
            grid.pipes.map {
                OptimizedPipeResponse(
                    it.id,
                    it.type.diameter,
                    it.volumeFlow,
                    it.heatLoss,
                    it.pipePressureLoss,
                    it.totalPressureLoss,
                    it.totalPumpPower
                )
            },
            grid.nodes.map {
                OptimizedNodeResponse(
                    it.id,
                    it.energyDemand,
                    it.pressureLoss,
                    it.pumpPower
                )
            }
        )
    }
}