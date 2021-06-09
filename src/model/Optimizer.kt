package de.fhac.ewi.model

import de.fhac.ewi.util.DoubleFunction
import de.fhac.ewi.util.round

class Optimizer(
    val pipeInvestCostFunc: DoubleFunction, // invest costs (1x) for a one meter pipe as f(diameter) = €
    val pipeOperationCostFunc: DoubleFunction, // annual operation cost for a grid as f(sum of pipeInvestCost) = €
    val pumpInvestCostFunc: DoubleFunction, // invest costs (1x) for a pump as f(Leistung) = €
    val heatGenerationCost: Double, // costs for generating heat losses
    val lifespanOfResources: Double, // Livetime of ressources. Needed for total cost calculation
    val electricityCost: Double, // ct/kWh [for pump station]
    val electricalEfficiency: Double, // for pump
    val hydraulicEfficiency: Double, // for pump
) {


    fun optimize(grid: Grid): Double {
        var currentCost = Double.MAX_VALUE

        // Reset all diameters
        grid.pipes.forEach { it.diameter = DIAMETERS.last() }

        // until everything is optimized
        optimizer@ while (true) {
            // check for every pipe, if another diameter would be better in total costs
            for (pipe in grid.pipes) {
                for (diameter in DIAMETERS) {
                    val lastDiameter = pipe.diameter
                    pipe.diameter = diameter
                    val newCost = calculateCosts(grid).total
                    if (newCost < currentCost) {
                        currentCost = newCost
                        continue@optimizer
                    } else {
                        pipe.diameter = lastDiameter
                    }
                }
            }
            break
        }

        return currentCost
    }


    fun calculateCosts(grid: Grid): Costs {
        val pipeInvestCost = grid.pipes.sumOf { pipeInvestCostFunc(it.diameter) * it.length }
        val pipeOperationCost = pipeOperationCostFunc(pipeInvestCost)

        val pumpInvestCost = pumpInvestCostFunc(grid.neededPumpPower / hydraulicEfficiency)
        val pumpOperationCost = grid.input.neededPumpPower.sumOf { it / hydraulicEfficiency / electricalEfficiency * electricityCost }

        val investCost = pipeInvestCost + pumpInvestCost
        val operationCost = pipeOperationCost + pumpOperationCost + 0 // TODO heat loss

        val total = investCost + operationCost * lifespanOfResources
        return Costs(pipeInvestCost, pipeOperationCost, pumpInvestCost, pumpOperationCost, total)
    }

    companion object {
        val DIAMETERS = listOf(20, 25, 32, 40, 50, 65, 80, 100, 125, 150, 200, 250).map { it / 1000.0 } // in m
    }
}