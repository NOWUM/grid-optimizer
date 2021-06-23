package de.fhac.ewi.model

import de.fhac.ewi.util.DoubleFunction
import kotlin.math.pow

class InvestmentParameter(
    pipeTypes: List<PipeType>, // possible pipe types that can be used in optimization
    val pipeOperationCostFunc: DoubleFunction, // annual operation cost for a grid as f(sum of pipeInvestCost) = €
    val pumpInvestCostFunc: DoubleFunction, // invest costs (1x) for a pump as f(Leistung in kW) = €
    val heatGenerationCost: Double, // costs for generating heat losses
    lifespanOfGrid: Double, // Lifespan of grid in years. Needed for invest cost calculation
    lifespanOfPump: Double, // Lifespan of pump in years. Needed for invest cost calculation
    val wacc: Double, // Zinsen
    val electricityCost: Double, // €/kWh [for pump station]
    val electricalEfficiency: Double, // for pump
    val hydraulicEfficiency: Double, // for pump
) {

    val pipeTypes: List<PipeType> = pipeTypes.sortedBy { it.diameter }
    val pipeAnnuityFactor = calculateAnnuityFactor(lifespanOfGrid)
    val pumpAnnuityFactor = calculateAnnuityFactor(lifespanOfPump)

    fun calculateCosts(grid: Grid): Costs {
        // Pipe costs
        val pipeInvestCostTotal = grid.pipes.sumOf { it.type.costPerMeter * it.length }
        val pipeInvestCostAnnuity = pipeInvestCostTotal * pipeAnnuityFactor
        val pipeOperationCost = pipeOperationCostFunc(pipeInvestCostTotal)

        val pumpInvestCostTotal = pumpInvestCostFunc(grid.neededPumpPower / hydraulicEfficiency / electricalEfficiency / 1_000)
        val pumpInvestCostAnnuity = pumpInvestCostTotal * pumpAnnuityFactor
        val pumpOperationCost =
            grid.input.pumpPower.sumOf { it / hydraulicEfficiency / electricalEfficiency / 1_000 * electricityCost }

        val heatLossCost = grid.totalHeatLoss / 1_000 * heatGenerationCost

        val investCostAnnuity = pipeInvestCostAnnuity + pumpInvestCostAnnuity
        val operationCostPerYear = pipeOperationCost + pumpOperationCost + heatLossCost

        val total = investCostAnnuity + operationCostPerYear
        return Costs(
            pipeInvestCostTotal, pipeInvestCostAnnuity, pipeOperationCost,
            pumpInvestCostTotal, pumpInvestCostAnnuity, pumpOperationCost,
            heatLossCost, total
        )
    }

    private fun calculateAnnuityFactor(lifespan: Double): Double =
        (1.0 + wacc / 100.0).pow(lifespan) * (wacc / 100.0) / ((1.0 + wacc / 100.0).pow(lifespan) - 1)
}