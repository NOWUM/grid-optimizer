package de.fhac.ewi.model

import de.fhac.ewi.services.HeatDemandService
import de.fhac.ewi.services.LoadProfileService
import de.fhac.ewi.services.TemperatureTimeSeriesService
import de.fhac.ewi.util.*
import org.junit.Test
import kotlin.test.assertEquals


class GridOptimizerTest {

    private val timeSeriesService = TemperatureTimeSeriesService(loadTemperatureTimeSeries())
    private val loadProfileService = LoadProfileService(loadHProfiles())
    private val heatDemandService = HeatDemandService(timeSeriesService, loadProfileService)

    private val investParameter = InvestmentParameter(
        createStreetPipes(), // types of pipes that can be used
        { invest -> invest * 0.01 }, // operating cost for grid based on invest cost
        { pumpPower -> 500.0 + pumpPower * 500 }, // invest cost for pump based on pump power f(kW) = €
        0.07, // (Kosten Erzeugung Wärmeverluste)
        40.0, // years for grid
        10.0, // years for pump
        1.75, // Zinsen in %
        0.3, // for pump operation per kWh
        0.9, // for pump
        0.60 // for pump
    )

    @Test
    fun testSimpleGrid() {
        val grid = createSimpleGrid()
        val optimizer = callOptimizer(grid)
        assertEquals(15296.67, optimizer.gridCosts.totalPerYear.round(2))
    }

    @Test
    fun testMediumGrid() {
        val grid = createMediumGrid()
        val optimizer = callOptimizer(grid)
        assertEquals(37947.67, optimizer.gridCosts.totalPerYear.round(2))
    }


    private fun callOptimizer(grid: Grid): Optimizer {
        val optimizer = Optimizer(grid, investParameter)
        optimizer.optimize()

        println("=== Optimization of Grid ===")

        println("> Grid Layout\n${grid.gridTreeString()}\n")

        println("> Grid Statistics\n" +
                ">> Nodes: ${grid.nodes.size} with a total energy demand of ${(grid.totalOutputEnergy / 1_000_000).round(3)} MWh\n" +
                ">> Pipes: ${grid.pipes.size} with a total length of ${grid.pipes.sumOf { it.length }} meter\n" +
                ">> Wärmeverlust: ${(grid.totalHeatLoss / 1_000_000).round(3)} MWh (${((grid.totalHeatLoss / (grid.totalHeatLoss + grid.totalOutputEnergy)) * 100).round(1)} %)\n" +
                ">> Druckverlust: ${grid.input.pressureLoss.maxOrNull()?.round(2)} Bar (max)\n" +
                ">> Volumenstrom: ${String.format("%.6f", grid.input.volumeFlow.maxOrNull())} m^3/s (max)\n" +
                ">> Pumpleistung: ${(grid.neededPumpPower / 1_000).round(3)} kW (max)\n")

        println("> Costs\n" +
                ">> Pipe Invest (Gesamt)   : ${optimizer.gridCosts.pipeInvestCostTotal.round(2).toString().padStart(8)} €\n" +
                ">> Pipe Invest (Annuität) : ${optimizer.gridCosts.pipeInvestCostAnnuity.round(2).toString().padStart(8)} €\n" +
                ">> Pipe Operation pro Jahr: ${optimizer.gridCosts.pipeOperationCost.round(2).toString().padStart(8)} €\n" +
                ">> Pump Invest (Gesamt)   : ${optimizer.gridCosts.pumpInvestCostTotal.round(2).toString().padStart(8)} €\n" +
                ">> Pump Invest (Annuität) : ${optimizer.gridCosts.pumpInvestCostAnnuity.round(2).toString().padStart(8)} €\n" +
                ">> Pump Operation pro Jahr: ${optimizer.gridCosts.pumpOperationCost.round(2).toString().padStart(8)} €\n" +
                ">> Wärmeverlust pro Jahr  : ${optimizer.gridCosts.heatLossCost.round(2).toString().padStart(8)} €\n" +
                "\n" +
                ">> Gesamtkosten pro Jahr  : ${optimizer.gridCosts.totalPerYear.round(2)} €")

        return optimizer
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
        val heatDemand = heatDemandService.createCurve(50_000_000.0, "EFH", "Schemm 2018")
        grid.addOutputNode("3", heatDemand, 1.0)
        val heatDemand2 = heatDemandService.createCurve(60_000_000.0, "EFH", "Schemm 2018")
        grid.addOutputNode("4", heatDemand2, 1.0)
        grid.addPipe("P1", "1", "2", 100.0, 0.6)
        grid.addPipe("P2", "2", "3", 50.0, 0.6)
        grid.addPipe("P3", "2", "4", 20.0, 0.6)
        return grid
    }

    private fun createMediumGrid(): Grid {
        val grid = Grid()
        grid.addInputNode(
            "#1",
            timeSeriesService.getSeries("DWD Koeln Bonn 2018"),
            "75".toDoubleFunction(),
            "60".toDoubleFunction()
        )
        grid.addIntermediateNode("#2")
        val heatDemand = heatDemandService.createCurve(50_000_000.0, "EFH", "DWD Koeln Bonn 2018")
        grid.addOutputNode("#2.1", heatDemand, 1.0)
        val heatDemand2 = heatDemandService.createCurve(60_000_000.0, "EFH", "DWD Koeln Bonn 2018")
        grid.addOutputNode("#2.2", heatDemand2, 1.0)
        grid.addPipe("P1", "#1", "#2", 100.0, 0.6)
        grid.addPipe("P2", "#2", "#2.1", 50.0, 0.6)
        grid.addPipe("P3", "#2", "#2.2", 20.0, 0.6)

        grid.addIntermediateNode("#3")
        val heatDemand3 = heatDemandService.createCurve(50_000_000.0, "EFH", "DWD Koeln Bonn 2018")
        grid.addOutputNode("#3.1", heatDemand3, 1.0)
        val heatDemand4 = heatDemandService.createCurve(60_000_000.0, "EFH", "DWD Koeln Bonn 2018")
        grid.addOutputNode("#3.2", heatDemand4, 1.0)
        grid.addPipe("P4", "#1", "#3", 100.0, 0.6)
        grid.addPipe("P5", "#3", "#3.1", 60.0, 0.6)
        grid.addPipe("P6", "#3", "#3.2", 10.0, 0.6)
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