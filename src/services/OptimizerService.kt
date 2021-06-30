package de.fhac.ewi.services

import de.fhac.ewi.dto.*
import de.fhac.ewi.model.Grid
import de.fhac.ewi.model.InvestmentParameter
import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.PipeType
import de.fhac.ewi.util.catchParseError
import de.fhac.ewi.util.toDoubleFunction
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

class OptimizerService {

    private val optimizations = mutableMapOf<String, Optimizer>()

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


    suspend fun optimize(grid: Grid, investmentParameter: InvestmentParameter): String = coroutineScope {
        val optimizer = Optimizer(grid, investmentParameter)
        val uuid = UUID.randomUUID().toString()
        optimizations[uuid] = optimizer
        launch {
            optimizer.optimize()
        }
        return@coroutineScope uuid
    }

    fun getStatus(id: String?): OptimizationStatusResponse {
        val optimizer = optimizations[id] ?: throw IllegalArgumentException("No optimizer found for id $id.")
        return OptimizationStatusResponse(id!!, optimizer.completed, optimizer.numberOfTypeChecks, optimizer.numberOfUpdates)
    }

    fun getPipe(id: String?, pipeId: String?): OptimizedPipeResponse {
        val optimizer = optimizations[id] ?: throw IllegalArgumentException("No optimizer found for id $id.")
        val pipe = optimizer.grid.pipes.singleOrNull { it.id == pipeId }
            ?: throw IllegalArgumentException("Pipe $pipeId not found.")
        return OptimizedPipeResponse(
            pipe.id,
            pipe.type.diameter,
            pipe.massenstrom,
            pipe.volumeFlow,
            pipe.heatLoss,
            pipe.pipePressureLoss,
            pipe.totalPressureLoss,
            pipe.totalPumpPower
        )
    }

    fun getNode(id: String?, nodeId: String?): OptimizedNodeResponse {
        val optimizer = optimizations[id] ?: throw IllegalArgumentException("No optimizer found for id $id.")
        val node = optimizer.grid.nodes.singleOrNull { it.id == nodeId }
            ?: throw IllegalArgumentException("Node $nodeId not found.")
        return OptimizedNodeResponse(
            node.id,
            node.energyDemand,
            node.massenstrom,
            node.pressureLoss,
            node.pumpPower,
            node.flowInTemperature,
            node.flowOutTemperature,
            node.energyDemand.sum(),
            node.pumpPower.maxOrNull()!!,
            node.pressureLoss.maxOrNull()!!
        )
    }

    fun getOverview(id: String?): OptimizationOverviewResponse {
        val optimizer = optimizations[id] ?: throw IllegalArgumentException("No optimizer found for id $id.")
        return OptimizationOverviewResponse(
            optimizer.gridCosts,
            optimizer.grid.criticalPath.map { it.id },
            optimizer.grid.pipes.map { OptimizedPipeTypeResponse(it.id, it.type.diameter) }
        )
    }
}