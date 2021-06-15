package de.fhac.ewi.model

import de.fhac.ewi.services.HeatDemandService
import de.fhac.ewi.services.LoadProfileService
import de.fhac.ewi.services.TemperatureTimeSeriesService
import de.fhac.ewi.util.loadHProfiles
import de.fhac.ewi.util.loadTemperatureTimeSeries
import de.fhac.ewi.util.round
import de.fhac.ewi.util.toDoubleFunction
import org.junit.Test


class GridOptimizerTest {

    private val timeSeriesService = TemperatureTimeSeriesService(loadTemperatureTimeSeries())
    private val loadProfileService = LoadProfileService(loadHProfiles())
    private val heatDemandService = HeatDemandService(timeSeriesService, loadProfileService)

    @Test
    fun testOptimizer() {
        val grid = createSimpleGrid()
        val pipeTypes = createStreetPipes()
        val optimizer = Optimizer(
            pipeTypes, // types of pipes that can be used
            { invest -> invest * 0.01 }, // operating cost for grid based on invest cost
            { pumpPower -> 500.0 + pumpPower * 4}, // invest cost for pump based on pump power
            0.05, // unused (Kosten Erzeugung Wärmeverluste)
            40.0, // years for grid
            10.0, // years for pump
            5.0, // years for including operation costs
            1.75, // Zinsen in %
            0.3, // for pump operation per kWh
            0.9, // for pump
            0.60 // for pump
        )

        optimizer.optimize(grid)

        val costs = optimizer.calculateCosts(grid)

        println("> Grid costs u ${costs.totalPerYear.round(2)} €")
        println(">> ${grid.pipes.sumOf { it.length }} m of pipes cost ${costs.pipeInvestCostTotal.round(2)} €.")
        println(">> Pump with power of ${grid.neededPumpPower.round(3)} Watt for maximum pressure loss of ${grid.input.connectedPressureLoss.maxOrNull()?.round(3)} Bar cost ${costs.pumpInvestCostTotal.round(2)} €.")
        grid.pipes.forEach { println("${it.id} should have a diameter of ${it.type.diameter}") }
        println(costs)
    }


    private fun createSimpleGrid(): Grid {
        val grid = Grid()
        grid.addInputNode("1", "80".toDoubleFunction(), "60".toDoubleFunction())
        grid.addIntermediateNode("2")
        val heatDemand = heatDemandService.createCurve(50_000.0, "EFH", "Schemm 2018")
        grid.addOutputNode("3", heatDemand, 1.0)
        val heatDemand2 = heatDemandService.createCurve(60_000.0, "EFH", "Schemm 2018")
        grid.addOutputNode("4", heatDemand2, 1.0)
        grid.addPipe("P1", "1", "2", 100.0)
        grid.addPipe("P2", "2", "3", 50.0)
        grid.addPipe("P3", "2", "4", 250.0)
        return grid
    }

    private fun createStreetPipes(): List<PipeType> {
        // Quelle https://www.ingenieur.de/fachmedien/bwk/energieversorgung/dimensionierung-von-fernwaermenetzen/
        return listOf(
            PipeType(0.020, 391.0),
            PipeType(0.025, 396.0),
            PipeType(0.032, 422.0),
            PipeType(0.040, 437.0),
            PipeType(0.050, 495.0),
            PipeType(0.065, 537.0),
            PipeType(0.080, 616.0),
            PipeType(0.100, 790.0),
            PipeType(0.125, 912.0),
            PipeType(0.150, 1101.0),
            PipeType(0.200, 1311.0),
            PipeType(0.250, 1755.0)
        )
    }
}