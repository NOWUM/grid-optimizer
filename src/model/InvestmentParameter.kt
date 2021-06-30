package de.fhac.ewi.model

import de.fhac.ewi.util.DoubleFunction
import kotlin.math.pow

class InvestmentParameter(
    pipeTypes: List<PipeType>, // possible pipe types that can be used in optimization
    val pipeOperationCostFunc: DoubleFunction, // annual operation cost for a grid as f(sum of pipeInvestCost) = €
    val pumpInvestCostFunc: DoubleFunction, // invest costs (1x) for a pump as f(Leistung in kW) = €
    val heatGenerationCost: Double, // costs for generating heat losses
    val lifespanOfGrid: Double, // Lifespan of grid in years. Needed for invest cost calculation
    val lifespanOfPump: Double, // Lifespan of pump in years. Needed for invest cost calculation
    val wacc: Double, // Zinsen
    val electricityCost: Double, // €/kWh [for pump station]
    val electricalEfficiency: Double, // for pump
    val hydraulicEfficiency: Double, // for pump
) {

    val pipeTypes: List<PipeType> = pipeTypes.sortedBy { it.diameter }
    val pipeAnnuityFactor = calculateAnnuityFactor(lifespanOfGrid)
    val pumpAnnuityFactor = calculateAnnuityFactor(lifespanOfPump)

    fun calculateCosts(grid: Grid): Costs =
        calculateCosts(grid.pipes, grid.neededPumpPower, grid.input.pumpPower.sum(), grid.totalHeatLoss)

    fun calculateCosts(node: Node): Costs =
        calculateCosts(node.connectedChildPipes, node.pumpPower.maxOrNull()!!, node.pumpPower.sum(), node.totalHeatLoss)

    /**
     * Berechnung der Kosten.
     * @param pipes List<Pipe> - Rohrleitungen
     * @param maxPumpPower Double - maximal benötigte Pumpleistung in W
     * @param pumpPower Double - benötigte Pumpleistung im gesamten Jahr in Wh
     * @param heatLoss Double - Wärmeverlust in W
     * @return Costs - Kosten in €
     */
    private fun calculateCosts(pipes: List<Pipe>, maxPumpPower: Double, pumpPower: Double, heatLoss: Double): Costs {
        val pipeInvestCostTotal = pipes.sumOf { it.investCost }
        val pipeInvestCostAnnuity = pipeInvestCostTotal * pipeAnnuityFactor
        val pipeOperationCost = pipeOperationCostFunc(pipeInvestCostTotal)

        val pumpInvestCostTotal = pumpInvestCostFunc(maxPumpPower / 1_000 / hydraulicEfficiency / electricalEfficiency )
        val pumpInvestCostAnnuity = pumpInvestCostTotal * pumpAnnuityFactor
        val pumpOperationCost = pumpPower / 1_000 / hydraulicEfficiency / electricalEfficiency  * electricityCost

        val heatLossCost = heatLoss / 1_000 * heatGenerationCost

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