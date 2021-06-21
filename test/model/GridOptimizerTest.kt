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
        val investParameter = InvestmentParameter(
            pipeTypes, // types of pipes that can be used
            { invest -> invest * 0.01 }, // operating cost for grid based on invest cost
            { pumpPower -> 500.0 + pumpPower / 1000 * 500 }, // invest cost for pump based on pump power
            0.05, // unused (Kosten Erzeugung Wärmeverluste)
            40.0, // years for grid
            10.0, // years for pump
            1.75, // Zinsen in %
            0.3, // for pump operation per kWh
            0.9, // for pump
            0.60 // for pump
        )

        val optimizer = Optimizer(grid, investParameter)
        optimizer.optimize()


        println("> Connected energy demand (OutputNodes): ${(grid.totalOutputEnergy / 1_000).round(3)} kWh")
        println("> Heat loss in all pipes: ${(grid.totalHeatLoss / 1_000).round(3)} kWh")
        println("> Checked ${optimizer.numberOfTypeChecks} pipe types and made ${optimizer.numberOfUpdates} for perfect grid.")

        println("> Grid costs u ${optimizer.gridCosts.totalPerYear.round(2)} € per year.")
        println(">> ${grid.pipes.sumOf { it.length }} m of pipes cost ${optimizer.gridCosts.pipeInvestCostTotal.round(2)} €.")
        println(
            ">> Pump with power of ${grid.neededPumpPower.round(3)} Watt for maximum pressure loss of ${
                grid.input.pressureLoss.maxOrNull()?.round(3)
            } Bar cost ${optimizer.gridCosts.pumpInvestCostTotal.round(2)} €."
        )
        println(
            ">> Heat loss of ${(grid.totalOutputEnergy / 1_000).round(3)} kW cost ${
                optimizer.gridCosts.heatLossCost.round(
                    2
                )
            } €."
        )
        grid.pipes.forEach { println("${it.id} should have a diameter of ${it.type.diameter}") }
        println(optimizer.gridCosts)
    }


    private fun createSimpleGrid(): Grid {
        val grid = Grid()
        grid.addInputNode(
            "1",
            timeSeriesService.getSeries("Schemm 2018"),
            "80".toDoubleFunction(),
            "60".toDoubleFunction()
        )
        grid.addIntermediateNode("2")
        val heatDemand = heatDemandService.createCurve(50_000.0, "EFH", "Schemm 2018")
        grid.addOutputNode("3", heatDemand, 1.0)
        val heatDemand2 = heatDemandService.createCurve(60_000.0, "EFH", "Schemm 2018")
        grid.addOutputNode("4", heatDemand2, 1.0)
        grid.addPipe("P1", "1", "2", 100.0, 0.6)
        grid.addPipe("P2", "2", "3", 50.0, 0.6)
        grid.addPipe("P3", "2", "4", 250.0, 0.6)
        return grid
    }

    private fun createStreetPipes(): List<PipeType> {
        // Quelle https://www.ingenieur.de/fachmedien/bwk/energieversorgung/dimensionierung-von-fernwaermenetzen/
        // Dämmdicke: https://www.ikz.de/uploads/media/50-55_Daemmstandards.pdf
        // Seite 91 https://www.energie-zentralschweiz.ch/fileadmin/user_upload/Downloads/Planungshilfen/Planungshandbuch_Fernwarrme_V1.0x.pdf
        return listOf(
            PipeType(0.020, 391.0, 0.04, 0.2),
            PipeType(0.025, 396.0, 0.04, 0.2),
            PipeType(0.032, 422.0, 0.04, 0.2),
            PipeType(0.040, 437.0, 0.04, 0.2),
            PipeType(0.050, 495.0, 0.04, 0.2),
            PipeType(0.065, 537.0, 0.04, 0.2),
            PipeType(0.080, 616.0, 0.04, 0.2),
            PipeType(0.100, 790.0, 0.04, 0.2),
            PipeType(0.125, 912.0, 0.04, 0.2),
            PipeType(0.150, 1101.0, 0.04, 0.2),
            PipeType(0.200, 1311.0, 0.04, 0.2),
            PipeType(0.250, 1755.0, 0.04, 0.2)
        )
    }
}