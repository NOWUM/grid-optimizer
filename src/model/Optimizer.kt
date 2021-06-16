package de.fhac.ewi.model

import de.fhac.ewi.util.DoubleFunction
import kotlin.math.ceil

class Optimizer(
    val pipeTypes: List<PipeType>, // invest costs (1x) for a one meter pipe as f(diameter) = €
    val pipeOperationCostFunc: DoubleFunction, // annual operation cost for a grid as f(sum of pipeInvestCost) = €
    val pumpInvestCostFunc: DoubleFunction, // invest costs (1x) for a pump as f(Leistung in Watt) = €
    val heatGenerationCost: Double, // costs for generating heat losses
    val lifespanOfGrid: Double, // Lifespan of grid. Needed for invest cost calculation
    val lifespanOfPump: Double, // Lifespan of pump. Needed for invest cost calculation
    val yearsOfOperation: Double, // Needed for total cost calculation
    val electricityCost: Double, // €/kWh [for pump station]
    val electricalEfficiency: Double, // for pump
    val hydraulicEfficiency: Double, // for pump
) {


    fun optimize(grid: Grid): Double {
        var currentCost = Double.MAX_VALUE

        // Reset all diameters
        grid.pipes.forEach { it.type = PipeType.UNDEFINED }

        // until everything is optimized
        var anyPipeUpdated: Boolean
        optimizer@ do {
            anyPipeUpdated = false
            // check for every pipe, if another diameter would be better in total costs
            pipeCheck@ for (pipe in grid.pipes) {
                for (type in pipeTypes) {
                    val lastType = pipe.type
                    pipe.type = type
                    val newCost = calculateCosts(grid).total
                    if (newCost < currentCost) {
                        currentCost = newCost
                        anyPipeUpdated = true
                        continue@pipeCheck
                    } else {
                        pipe.type = lastType
                    }
                }
            }
        } while (anyPipeUpdated)

        return currentCost
    }


    fun calculateCosts(grid: Grid): Costs {
        val pipeInvestCost = grid.pipes.sumOf { it.type.costPerMeter * it.length }
        val pipeOperationCost = pipeOperationCostFunc(pipeInvestCost)

        val pumpInvestCost = pumpInvestCostFunc(grid.neededPumpPower / hydraulicEfficiency)
        val pumpOperationCost = grid.input.neededPumpPower.sumOf { it / hydraulicEfficiency / electricalEfficiency / 1_000 * electricityCost }

        val heatLossCost = grid.pipes.sumOf { it.pipeHeatLoss.sum() } / 1_000 * heatGenerationCost

        val investCost = pipeInvestCost * ceil(yearsOfOperation / lifespanOfGrid) + pumpInvestCost * ceil(yearsOfOperation / lifespanOfPump)
        val operationCostPerYear = pipeOperationCost + pumpOperationCost + heatLossCost

        val total = investCost + operationCostPerYear * yearsOfOperation
        return Costs(pipeInvestCost, pipeOperationCost, pumpInvestCost, pumpOperationCost, heatLossCost, total)
    }
}