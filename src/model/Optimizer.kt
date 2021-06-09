package de.fhac.ewi.model

import de.fhac.ewi.util.DoubleFunction
import de.fhac.ewi.util.round

class Optimizer(
    val grid: Grid, // Grid to optimize
    val pipeInvestCostFunc: DoubleFunction, // invest costs (1x) for a one meter pipe as f(diameter) = €
    val pipeOperationCostFunc: DoubleFunction, // annual operation cost for a grid as f(sum of pipeInvestCost) = €
    val pumpInvestCostFunc: DoubleFunction, // invest costs (1x) for a pump as f(Leistung) = €
    val heatGenerationCostFunc: DoubleFunction, // costs for generating heat losses
    val lifespanOfResources: Double, // Livetime of ressources. Needed for total cost calculation
    val electricityCost: Double, // ct/kWh [for pump station]
    val electricalEfficiency: Double, // for pump
    val hydraulicEfficiency: Double, // for pump
) {


    fun optimize(): Double {
        var currentCost = Double.MAX_VALUE

        // Reset all diameters
        grid.pipes.forEach { it.diameter = DIAMETERS.last() }

        // until everything is optimized
        optimizer@ while (true) {
            println("Current max power loss ${calculateMaximumPumpPower()}")
            // check for every pipe, if another diameter would be better in total costs
            for (pipe in grid.pipes) {
                for (diameter in DIAMETERS) {
                    println("Testing diameter $diameter for ${pipe.id}...")
                    val lastDiameter = pipe.diameter
                    pipe.diameter = diameter
                    val newCost = calculateCurrentTotalCost()
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

    fun calculateCurrentTotalCost(): Double {
        val pipeInvestCost = grid.pipes.sumOf { pipeInvestCostFunc(it.diameter) * it.length }
        val pipeOperationCost = pipeOperationCostFunc(pipeInvestCost)

        val pumpInvestCost = pumpInvestCostFunc(calculateMaximumPumpPower() / hydraulicEfficiency)
        val pumpOperationCost = grid.input.neededPumpPower.sumOf { it / hydraulicEfficiency / electricalEfficiency * electricityCost }

        val investCost = pipeInvestCost + pumpInvestCost
        val operationCost = pipeOperationCost + pumpOperationCost + 0 // TODO heat loss

        val total = investCost + operationCost * lifespanOfResources
        println("=== Cost Overview ===\n" +
                " > Invest: ${investCost.round(2)} €\n" +
                " >> Pipe invest: ${pipeInvestCost.round(2)} € (for ${grid.pipes.sumOf { it.length }.round(2)} meter)\n" +
                " >> Pump invest: ${pumpInvestCost.round(2)} € (for ${calculateMaximumPumpPower().round(2)} needed pump power for pressure loss of ${grid.input.connectedPressureLoss.maxOrNull()} Bar)\n" +
                " > Operating: ${operationCost.round(2)} € per year\n" +
                " >> Pipe operation: ${pipeOperationCost.round(2)} €\n" +
                " >> Pump operation: ${pumpOperationCost.round(2)} €\n" +
                " > Total of ${total.round(2)} € for $lifespanOfResources years")
        return total
    }

    private fun calculateMaximumPumpPower(): Double {
        return grid.input.neededPumpPower.maxOrNull()!!
    }


    companion object {
        val DIAMETERS = listOf(20, 25, 32, 40, 50, 65, 80, 100, 125, 150, 200, 250).map { it / 1000.0 } // in m
    }
}