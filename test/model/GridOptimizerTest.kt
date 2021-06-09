package de.fhac.ewi.model

import de.fhac.ewi.services.HeatDemandService
import de.fhac.ewi.services.LoadProfileService
import de.fhac.ewi.services.TemperatureTimeSeriesService
import de.fhac.ewi.util.loadHProfiles
import de.fhac.ewi.util.loadTemperatureTimeSeries
import de.fhac.ewi.util.toDoubleFunction
import org.junit.Test
import kotlin.math.pow


class GridOptimizerTest {

    private val timeSeriesService = TemperatureTimeSeriesService(loadTemperatureTimeSeries())
    private val loadProfileService = LoadProfileService(loadHProfiles())
    private val heatDemandService = HeatDemandService(timeSeriesService, loadProfileService)

    @Test
    fun testOptimizer() {
        val grid = createSimpleGrid()
        val optimizer = Optimizer(
            grid,
            { diameter -> 250 + diameter * 3 }, // invest cost for pipe per meter
            { invest -> invest * 0.01 }, // operating cost for grid based on invest cost
            { pumpPower -> 500.0 + pumpPower * 4}, // invest cost for pump based on pump power
            { need -> need * 0.3}, // unused (Kosten Erzeugung Wärmeverluste)
            15.0, // years to calculate
            0.3, // for pump operation
            0.65, // for pump
            0.60 // for pump
        )

        optimizer.optimize()

        println("Grid costs u ${optimizer.calculateCurrentTotalCost()}")
        grid.pipes.forEach { println("${it.id} should have a diameter of ${it.diameter}") }
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
}