package de.fhac.ewi.model

import de.fhac.ewi.util.DoubleFunction
import kotlin.math.pow

class Optimizer(
    val pipeTypes: List<PipeType>, // invest costs (1x) for a one meter pipe as f(diameter) = €
    val pipeOperationCostFunc: DoubleFunction, // annual operation cost for a grid as f(sum of pipeInvestCost) = €
    val pumpInvestCostFunc: DoubleFunction, // invest costs (1x) for a pump as f(Leistung in Watt) = €
    val heatGenerationCost: Double, // costs for generating heat losses
    val lifespanOfGrid: Double, // Lifespan of grid. Needed for invest cost calculation
    val lifespanOfPump: Double, // Lifespan of pump. Needed for invest cost calculation
    val wacc: Double, // Zinsen
    val electricityCost: Double, // €/kWh [for pump station]
    val electricalEfficiency: Double, // for pump
    val hydraulicEfficiency: Double, // for pump
) {

    val pipeAnnuityFactor = calculateAnnuityFactor(lifespanOfGrid)
    val pumpAnnuityFactor = calculateAnnuityFactor(lifespanOfPump)

    fun optimize(grid: Grid): Double {
        var currentCost = Double.MAX_VALUE

        // Reset all diameters
        grid.pipes.forEach { it.type = PipeType.UNDEFINED }

        var pipeChecks = 0

        // until everything is optimized
        var anyPipeUpdated: Boolean
        optimizer@ do {
            anyPipeUpdated = false
            // check for every pipe, if another diameter would be better in total costs
            pipeCheck@ for (pipe in grid.pipes) {
                for (type in pipeTypes) {
                    pipeChecks++
                    val lastType = pipe.type
                    pipe.type = type
                    val newCost = calculateCosts(grid).totalPerYear
                    if (newCost < currentCost) {
                        currentCost = newCost
                        anyPipeUpdated = true
                    } else {
                        pipe.type = lastType
                    }
                }
            }
        } while (anyPipeUpdated)

        println("Checked $pipeChecks times for perfect pipe type.")

        return currentCost
    }


    fun calculateCosts(grid: Grid): Costs {
        // Pipe costs
        val pipeInvestCostTotal = grid.pipes.sumOf { it.type.costPerMeter * it.length }
        val pipeInvestCostAnnuity = pipeInvestCostTotal * pipeAnnuityFactor
        val pipeOperationCost = pipeOperationCostFunc(pipeInvestCostTotal)

        val pumpInvestCostTotal = pumpInvestCostFunc(grid.neededPumpPower / hydraulicEfficiency)
        val pumpInvestCostAnnuity = pumpInvestCostTotal * pumpAnnuityFactor
        val pumpOperationCost = grid.input.pumpPower.sumOf { it / hydraulicEfficiency / electricalEfficiency / 1_000 * electricityCost }

        val heatLossCost = grid.pipes.sumOf { it.heatLoss.sum() } / 1_000 * heatGenerationCost

        val investCostAnnuity = pipeInvestCostAnnuity + pumpInvestCostAnnuity
        val operationCostPerYear = pipeOperationCost + pumpOperationCost + heatLossCost

        val total = investCostAnnuity + operationCostPerYear
        return Costs(pipeInvestCostTotal, pipeInvestCostAnnuity, pipeOperationCost,
            pumpInvestCostTotal, pumpInvestCostAnnuity, pumpOperationCost,
           heatLossCost, total)
    }

    private fun calculateAnnuityFactor(lifespan: Double) : Double =
        (1.0 + wacc / 100.0).pow(lifespan) * (wacc / 100.0) / ((1.0 + wacc / 100.0).pow(lifespan) - 1)
}