package de.fhac.ewi.model

import com.google.gson.Gson
import de.fhac.ewi.dto.GridRequest
import de.fhac.ewi.model.strategies.DoNothing
import de.fhac.ewi.model.strategies.Strategy
import de.fhac.ewi.services.GridService
import de.fhac.ewi.services.HeatDemandService
import de.fhac.ewi.services.LoadProfileService
import de.fhac.ewi.services.TemperatureTimeSeriesService
import de.fhac.ewi.util.*
import org.junit.Test
import java.util.*
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
        assertEquals(5593.09, optimizer.gridCosts.totalPerYear.round(2))
    }

    @Test
    fun testMediumGrid() {
        val grid = createMediumGrid()
        val optimizer = callOptimizer(grid)
        assertEquals(11986.81, optimizer.gridCosts.totalPerYear.round(2))
    }

    @Test
    fun testLargeGrid() {
        val grid = createLargeGrid()
        val optimizer = callOptimizer(grid)
        assertEquals(48883.56, optimizer.gridCosts.totalPerYear.round(2))
    }

    @Test
    fun testOptimizationPipeReset() {
        val grid = createMediumGrid()
        val optimizer = callOptimizer(grid)
        val newCalculation = investParameter.calculateCosts(grid)
        // If here is any differenz then we might have a bug resetting the pipes
        assertEquals(newCalculation, optimizer.gridCosts)
    }

    @Test
    fun testPipeChange() {
        val grid = createLargeGrid()
        grid.pipes.forEach { it.type = investParameter.pipeTypes.first() }
        val costPrevious = investParameter.calculateCosts(grid)
        grid.pipes.forEach { it.type = investParameter.pipeTypes.last() }
        val costsLarge = investParameter.calculateCosts(grid)
        val costsLargeTwice = investParameter.calculateCosts(grid)
        assertEquals(costsLarge, costsLargeTwice, "Costs should be on second check the same as first check.")
        grid.pipes.forEach { it.type = investParameter.pipeTypes.first() }
        val costAfter = investParameter.calculateCosts(grid)
        assertEquals(costPrevious, costAfter, "Costs should be after pipe change the same as before with same pipes.")
    }

    @Test
    fun testGroupA4Grid() {
        val grid = createGroupA4Grid()
        val optimizer = callOptimizer(grid)
        assertEquals(582730.63, optimizer.gridCosts.totalPerYear.round(2))
    }

    @Test
    fun calculateCostsGroupA4() {
        val grid = createGroupA4Grid()
        grid.setDiameter("v2-1623948434502-6584421336014", 0.400)
        grid.setDiameter("v2-1623948458559-3139937849080", 0.2)
        grid.setDiameter("v2-1623948615596-9013208595355", 0.065)
        grid.setDiameter("v2-1623948781959-6461274424182", 0.2)
        grid.setDiameter("v2-1623948850503-7199906600014", 0.15)
        grid.setDiameter("v2-1623948888657-1255433869119", 0.100)
        grid.setDiameter("v2-1623948916232-9530535697017", 0.40)
        grid.setDiameter("v2-1623948933229-3445270086926", 0.2)
        grid.setDiameter("v2-1623948948357-5792158181489", 0.08)
        grid.setDiameter("v2-1623948979515-7440844588284", 0.15) // ??
        grid.setDiameter("v2-1623949007333-3890636504980", 0.1) // ??
        grid.setDiameter("v2-1623949020704-1888345307649", 0.1)
        grid.setDiameter("v2-1623949040469-7981701803438", 0.35)
        grid.setDiameter("v2-1623949064745-3144362560075", 0.35)
        grid.setDiameter("v2-1623949166001-5868623589475", 0.3)
        grid.setDiameter("v2-1623949185608-4284341811845", 0.08)
        grid.setDiameter("v2-1623949203223-7316174764911", 0.3)
        grid.setDiameter("v2-1623949218134-8718118142529", 0.2)
        // Missing 28,42m Kölnstraße 14-19 (at the end)
        grid.setDiameter("v2-1623949273234-4405752756939", 0.15)
        grid.setDiameter("v2-1623949304200-7885690605181", 0.2)
        grid.setDiameter("v2-1623949319586-9883630363592", 0.15)
        grid.setDiameter("v2-1623949353412-8681006918965", 0.15)
        grid.setDiameter("v2-1623949375570-2446857374757", 0.15)
        grid.setDiameter("v2-1623949393378-4928790744688", 0.1)
        grid.setDiameter("v2-1623949418756-1510550035278", 0.065)
        grid.setDiameter("v2-1623949435568-3329269489872", 0.15)
        grid.setDiameter("v2-1623949446737-1302856814999", 0.10)
        grid.setDiameter("v2-1623949456551-3978600665438", 0.10)
        grid.setDiameter("v2-1623959200709-5126784923270", 0.08)

        // Hat Gruppe A4 so gewählt
        val housePipe = investParameter.pipeTypes.single { it.diameter == 0.05 }
        grid.pipes.filter { it.type == PipeType.UNDEFINED }.forEach {
            it.type = housePipe
        }

        val optimizer = callOptimizer(grid, listOf(DoNothing), false)
        assertEquals(780411.6, optimizer.gridCosts.totalPerYear.round(2))
    }

    /**
     * Set Pipe type to specific values.
     *
     * @receiver Grid
     * @param pipeId String - ID of pipe that ll be changed
     * @param diameter Double - Diameter in meter
     */
    private fun Grid.setDiameter(pipeId: String, diameter: Double) {
        pipes.single { it.id == pipeId }.type = investParameter.pipeTypes.single { it.diameter == diameter }
    }

    private fun callOptimizer(grid: Grid, strategies: List<Strategy> = emptyList(), resetBeforeStart: Boolean = true): Optimizer {
        val optimizer = Optimizer(grid, investParameter)
        if (strategies.isEmpty())
            optimizer.optimize(resetBeforeStart = resetBeforeStart)
        else optimizer.optimize(strategies, resetBeforeStart)

        println("=== Optimization of Grid ===")

        if (grid.pipes.size < 30)
            println("> Grid Layout\n${grid.gridTreeString()}\n")

        println(
            "> Perfekt Pipes\n${
                grid.pipes.joinToString("\n") {
                    ">> ${it.id.padEnd(3)} (${it.length.toString().padStart(5)} m) " +
                            "Ø ${(it.type.diameter * 1000).toInt().toString().padStart(3)} mm "
                }
            }\n"
        )
        with(grid.mostPressureLossNode) {
            println("> Critical Path (most pressure loss)\n" +
                    ">> Länge       : ${pathToSource.pathLength().round(2)} m\n" +
                    ">> Druckverlust: ${maxPressureLossInPath.round(3)} Bar gesamt, ${pathToSource.maxPipePressureLossPerMeter().round(3)} Pa/m im Pfad\n" +
                    ">> Pfad : ${pathToSource.reversed().map { "${it.id} (${it.length} m)" }}\n"
            )
        }
        with(grid.mostDistantNode) {
            println("> Longest Path\n" +
                    ">> Länge       : ${pathToSource.pathLength().round(2)} m\n" +
                    ">> Druckverlust: ${maxPressureLossInPath.round(3)} Bar gesamt, ${pathToSource.maxPipePressureLossPerMeter().round(3)} Pa/m im Pfad\n" +
                    ">> Pfad : ${pathToSource.reversed().map { "${it.id} (${it.length} m)" }}\n"
            )
        }

        println(
            "> Netzstatistiken\n" +
                    ">> Netzelemente: ${grid.nodes.size} mit einem Energiebedarf von ${grid.totalOutputEnergy.toMW()} MWh\n" +
                    ">> Leitungen   : ${grid.pipes.size} mit einer Länge von ${grid.pipes.sumOf { it.length }.round(2)} m\n" +
                    ">> Wärmeverlust: ${grid.totalHeatLoss.toMW()} MWh (${
                        ((grid.totalHeatLoss / (grid.totalHeatLoss + grid.totalOutputEnergy)) * 100).round(1)
                    } %)\n" +
                    ">> Druckverlust: ${grid.input.pressureLoss.maxOrElse().round(2)} Bar (max)\n" +
                    ">> Massenstrom : ${grid.input.massenstrom.maxOrElse().round(3)} kg/s (max)\n" +
                    ">> Volumenstrom: ${String.format(Locale.ENGLISH, "%.6f", grid.input.volumeFlow.maxOrElse())} m^3/s (max)\n" +
                    ">> Pumpleistung: ${(grid.neededPumpPower / 1_000).round(3)} kW (max)\n"
        )

        with(optimizer.gridCosts) {
            println(
                "> Kosten\n" +
                        ">> Pipe Invest (Gesamt)   : ${pipeInvestCostTotal.toEURString()}\n" +
                        ">> Pipe Invest (Annuität) : ${pipeInvestCostAnnuity.toEURString()}\n" +
                        ">> Pipe Operation pro Jahr: ${pipeOperationCost.toEURString()}\n" +
                        ">> Pump Invest (Gesamt)   : ${pumpInvestCostTotal.toEURString()}\n" +
                        ">> Pump Invest (Annuität) : ${pumpInvestCostAnnuity.toEURString()}\n" +
                        ">> Pump Operation pro Jahr: ${pumpOperationCost.toEURString()}\n" +
                        ">> Wärmeverlust pro Jahr  : ${heatLossCost.toEURString()}\n" +
                        "\n" +
                        ">> Gesamtkosten pro Jahr  : ${totalPerYear.toEURString()}"
            )
        }

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
        val timeSeriesString = "DWD Koeln Bonn 2018"
        grid.addInputNode(
            "#1",
            timeSeriesService.getSeries(timeSeriesString),
            "75".toDoubleFunction(),
            "60".toDoubleFunction()
        )

        val heatDemand = heatDemandService.createCurve(60_000_000.0, "EFH", timeSeriesString)
        val heatDemand2 = heatDemandService.createCurve(50_000_000.0, "EFH", timeSeriesString)
        grid.addIntermediateNode("#2")
        grid.addOutputNode("#2.1", heatDemand, 1.0)
        grid.addOutputNode("#2.2", heatDemand2, 1.0)
        grid.addPipe("P1", "#1", "#2", 100.0, 0.6)
        grid.addPipe("P2", "#2", "#2.1", 50.0, 0.6)
        grid.addPipe("P3", "#2", "#2.2", 20.0, 0.6)

        val heatDemand3 = heatDemandService.createCurve(50_000_000.0, "EFH", timeSeriesString)
        val heatDemand4 = heatDemandService.createCurve(120_000_000.0, "EFH", timeSeriesString)
        grid.addIntermediateNode("#3")
        grid.addOutputNode("#3.1", heatDemand3, 1.0)
        grid.addOutputNode("#3.2", heatDemand4, 1.0)
        grid.addPipe("P4", "#1", "#3", 100.0, 0.6)
        grid.addPipe("P5", "#3", "#3.1", 60.0, 0.6)
        grid.addPipe("P6", "#3", "#3.2", 10.0, 0.6)
        return grid
    }

    private fun createLargeGrid(): Grid {
        val grid = createMediumGrid()
        val timeSeriesString = "DWD Koeln Bonn 2018"
        grid.addIntermediateNode("#3.3")
        grid.addPipe("P7", "#3", "#3.3", 70.0, 0.6)
        val heatDemand = heatDemandService.createCurve(60_000_000.0, "EFH", timeSeriesString)
        grid.addOutputNode("#3.3.1", heatDemand, 0.6)
        grid.addOutputNode("#3.3.2", heatDemand, 0.9)
        grid.addOutputNode("#3.3.3", heatDemand, 0.6)
        grid.addOutputNode("#3.3.4", heatDemand, 0.9)
        grid.addPipe("P8", "#3.3", "#3.3.1", 10.0, 0.6)
        grid.addPipe("P9", "#3.3", "#3.3.2", 25.0, 0.6)
        grid.addPipe("P10", "#3.3", "#3.3.3", 17.0, 0.6)
        grid.addPipe("P11", "#3.3", "#3.3.4", 12.0, 0.6)

        val heatDemand2 = heatDemandService.createCurve(300_000_000.0, "MFH", timeSeriesString)
        grid.addIntermediateNode("#4")
        grid.addIntermediateNode("#4.1")
        grid.addIntermediateNode("#4.1.1")
        grid.addIntermediateNode("#4.2")
        grid.addPipe("P12", "#1", "#4", 150.0, 0.6)
        grid.addPipe("P13", "#4", "#4.1", 150.0, 0.6)
        grid.addPipe("P14", "#4.1", "#4.1.1", 50.0, 0.6)
        grid.addPipe("P16", "#4", "#4.2", 150.0, 0.6)
        grid.addOutputNode("#4.3", heatDemand2, 0.8)
        grid.addOutputNode("#4.4", heatDemand2, 0.8)
        grid.addPipe("P17", "#4", "#4.3", 30.0, 0.6)
        grid.addPipe("P18", "#4", "#4.4", 40.0, 0.6)
        grid.addOutputNode("#4.1.1.1", heatDemand2, 0.8)
        grid.addPipe("P19", "#4.1.1", "#4.1.1.1", 10.0, 0.6)
        grid.addOutputNode("#4.2.1", heatDemand2, 0.8)
        grid.addPipe("P20", "#4.2", "#4.2.1", 20.0, 0.6)

        return grid
    }

    private fun createGroupA4Grid(): Grid {
        val jsonString = "{\"pipes\":[{\"source\":\"v1-1621177974627-4475240057456\",\"sourceHandle\":\"a\",\"target\":\"v1-1621186044694-1690334569077\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623948434502-6584421336014\",\"length\":226.3,\"coverageHeight\":0.6,\"data\":{\"length\":226.3,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186044694-1690334569077\",\"sourceHandle\":\"b\",\"target\":\"v1-1621185871251-9919604707138\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623948458559-3139937849080\",\"length\":166.4,\"coverageHeight\":0.6,\"data\":{\"length\":166.4,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185871251-9919604707138\",\"sourceHandle\":\"a\",\"target\":\"v1-1621185898211-7831046798307\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623948615596-9013208595355\",\"length\":59.8,\"coverageHeight\":0.6,\"data\":{\"length\":59.8,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185871251-9919604707138\",\"sourceHandle\":\"b\",\"target\":\"v1-1621185911440-4962016131877\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623948781959-6461274424182\",\"length\":201.75,\"coverageHeight\":0.6,\"data\":{\"length\":201.75,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185911440-4962016131877\",\"sourceHandle\":\"a\",\"target\":\"v1-1621185949072-5762848366935\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623948850503-7199906600014\",\"length\":156.09,\"coverageHeight\":0.6,\"data\":{\"length\":156.09,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185911440-4962016131877\",\"sourceHandle\":\"b\",\"target\":\"v1-1621185960881-2278928340772\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623948888657-1255433869119\",\"length\":130.5,\"coverageHeight\":0.6,\"data\":{\"length\":130.5,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186044694-1690334569077\",\"sourceHandle\":\"a\",\"target\":\"v1-1621185980565-8743695485867\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623948916232-9530535697017\",\"length\":56.89,\"coverageHeight\":0.6,\"data\":{\"length\":56.89,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185980565-8743695485867\",\"sourceHandle\":\"b\",\"target\":\"v1-1621185993404-8663399000808\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623948933229-3445270086926\",\"length\":102.1,\"coverageHeight\":0.6,\"data\":{\"length\":102.1,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185993404-8663399000808\",\"sourceHandle\":\"a\",\"target\":\"v1-1621186006012-3164455616714\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623948948357-5792158181489\",\"length\":84.81,\"coverageHeight\":0.6,\"data\":{\"length\":84.81,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185993404-8663399000808\",\"sourceHandle\":\"b\",\"target\":\"v1-1621186022327-2899379456646\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623948979515-7440844588284\",\"length\":112.6,\"coverageHeight\":0.6,\"data\":{\"length\":112.6,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186022327-2899379456646\",\"sourceHandle\":\"a\",\"target\":\"v1-1621186037332-6365011053297\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949007333-3890636504980\",\"length\":112.6,\"coverageHeight\":0.6,\"data\":{\"length\":112.6,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186022327-2899379456646\",\"sourceHandle\":\"b\",\"target\":\"v1-1621186682472-9169377069812\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949020704-1888345307649\",\"length\":74.42,\"coverageHeight\":0.6,\"data\":{\"length\":74.42,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185980565-8743695485867\",\"sourceHandle\":\"a\",\"target\":\"v1-1621186098528-4388784622092\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949040469-7981701803438\",\"length\":76.44,\"coverageHeight\":0.6,\"data\":{\"length\":76.44,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186098528-4388784622092\",\"sourceHandle\":\"b\",\"target\":\"v1-1621186594588-2147702602155\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949064745-3144362560075\",\"length\":66.97,\"coverageHeight\":0.6,\"data\":{\"length\":66.97,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186594588-2147702602155\",\"sourceHandle\":\"b\",\"target\":\"v1-1621186629583-9384125582204\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949166001-5868623589475\",\"length\":88.37,\"coverageHeight\":0.6,\"data\":{\"length\":88.37,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186629583-9384125582204\",\"sourceHandle\":\"a\",\"target\":\"v1-1621186652759-4960965729761\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949185608-4284341811845\",\"length\":74.58,\"coverageHeight\":0.6,\"data\":{\"length\":74.58,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186629583-9384125582204\",\"sourceHandle\":\"b\",\"target\":\"v1-1621186812935-9961997802525\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949203223-7316174764911\",\"length\":96.04,\"coverageHeight\":0.6,\"data\":{\"length\":96.04,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186812935-9961997802525\",\"sourceHandle\":\"a\",\"target\":\"v1-1621186851560-6291085778803\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949218134-8718118142529\",\"length\":144.4,\"coverageHeight\":0.6,\"data\":{\"length\":144.4,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186851560-6291085778803\",\"sourceHandle\":\"b\",\"target\":\"v1-1621186913293-8028595782128\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949273234-4405752756939\",\"length\":184.14,\"coverageHeight\":0.6,\"data\":{\"length\":184.14,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186812935-9961997802525\",\"sourceHandle\":\"b\",\"target\":\"v1-1621186926888-1586477936382\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949304200-7885690605181\",\"length\":148.7,\"coverageHeight\":0.6,\"data\":{\"length\":148.7,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186926888-1586477936382\",\"sourceHandle\":\"b\",\"target\":\"v1-1621186938005-8206520472260\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949319586-9883630363592\",\"length\":196.38,\"coverageHeight\":0.6,\"data\":{\"length\":196.38,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186594588-2147702602155\",\"sourceHandle\":\"a\",\"target\":\"v1-1621186954765-7676883760911\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949353412-8681006918965\",\"length\":94.27,\"coverageHeight\":0.6,\"data\":{\"length\":94.27,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186954765-7676883760911\",\"sourceHandle\":\"b\",\"target\":\"v1-1621187146185-4822434399630\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949375570-2446857374757\",\"length\":95.41,\"coverageHeight\":0.6,\"data\":{\"length\":95.41,\"coverageHeight\":0.6}},{\"source\":\"v1-1621187146185-4822434399630\",\"sourceHandle\":\"a\",\"target\":\"v1-1621187165108-3673108523208\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949393378-4928790744688\",\"length\":68.95,\"coverageHeight\":0.6,\"data\":{\"length\":68.95,\"coverageHeight\":0.6}},{\"source\":\"v1-1621187146185-4822434399630\",\"sourceHandle\":\"b\",\"target\":\"v1-1621187186495-8646703581612\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949418756-1510550035278\",\"length\":52.34,\"coverageHeight\":0.6,\"data\":{\"length\":52.34,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186954765-7676883760911\",\"sourceHandle\":\"a\",\"target\":\"v1-1621187204200-7802240284225\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949435568-3329269489872\",\"length\":109.61,\"coverageHeight\":0.6,\"data\":{\"length\":109.61,\"coverageHeight\":0.6}},{\"source\":\"v1-1621187204200-7802240284225\",\"sourceHandle\":\"b\",\"target\":\"v1-1621187356851-6725650088354\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949446737-1302856814999\",\"length\":85.67,\"coverageHeight\":0.6,\"data\":{\"length\":85.67,\"coverageHeight\":0.6}},{\"source\":\"v1-1621187356851-6725650088354\",\"sourceHandle\":\"b\",\"target\":\"v1-1621187373900-1465702534212\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623949456551-3978600665438\",\"length\":142,\"coverageHeight\":0.6,\"data\":{\"length\":142,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186851560-6291085778803\",\"sourceHandle\":\"a\",\"target\":\"v1-1621186901941-8243265721583\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1623959200709-5126784923270\",\"length\":28.42,\"coverageHeight\":0.6,\"data\":{\"length\":28.42,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185993404-8663399000808\",\"sourceHandle\":\"c\",\"target\":\"v2-1624553358396-8564871909034\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624553629826-8421695294099\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621187373900-1465702534212\",\"sourceHandle\":\"a\",\"target\":\"v2-1624553630050-7882764107439\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624553709388-3663530935493\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186913293-8028595782128\",\"sourceHandle\":\"a\",\"target\":\"v2-1624553709595-7616411400162\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624553772757-6395556170580\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186938005-8206520472260\",\"sourceHandle\":\"c\",\"target\":\"v2-1624553772913-9482166813274\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624553908173-3521989427859\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186851560-6291085778803\",\"sourceHandle\":\"c\",\"target\":\"v2-1624554002738-6790837644495\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624554202200-4469366999155\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186901941-8243265721583\",\"sourceHandle\":\"b\",\"target\":\"v2-1624553908443-4220240968406\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624554212522-3933280681845\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621187356851-6725650088354\",\"sourceHandle\":\"a\",\"target\":\"v2-1624554212687-9413772475276\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624554290365-3625213510204\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621187186495-8646703581612\",\"sourceHandle\":\"a\",\"target\":\"v2-1624554290529-4405086332189\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624554398153-8867334431481\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621187165108-3673108523208\",\"sourceHandle\":\"b\",\"target\":\"v2-1624554398898-1650076799809\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624554480327-5454609824281\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621187204200-7802240284225\",\"sourceHandle\":\"a\",\"target\":\"v2-1624554480573-8589577025883\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624554543958-8377595330882\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621187146185-4822434399630\",\"sourceHandle\":\"b\",\"target\":\"v2-1624554544167-9319423815125\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624554616677-5201731268977\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186006012-3164455616714\",\"sourceHandle\":\"b\",\"target\":\"v2-1624554616858-7079921986001\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624554675997-7312827750075\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186594588-2147702602155\",\"sourceHandle\":\"a\",\"target\":\"v2-1624554676177-8969630706750\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624554736936-5377841767806\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186098528-4388784622092\",\"sourceHandle\":\"a\",\"target\":\"v2-1624554737173-3133658914524\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624554826872-4601103140377\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186926888-1586477936382\",\"sourceHandle\":\"b\",\"target\":\"v2-1624554827125-1938007856639\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624554963132-2185232827524\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185960881-2278928340772\",\"sourceHandle\":\"b\",\"target\":\"v2-1624554963380-7324701238204\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624555094635-7978212251803\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185949072-5762848366935\",\"sourceHandle\":\"b\",\"target\":\"v2-1624555094822-9587982611406\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624555140375-3083292798280\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186629583-9384125582204\",\"sourceHandle\":\"b\",\"target\":\"v2-1624555562187-8648690458487\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624555690467-8242584255914\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186812935-9961997802525\",\"sourceHandle\":\"c\",\"target\":\"v2-1624555690684-7538945303035\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624555733880-4290600801119\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185898211-7831046798307\",\"sourceHandle\":\"b\",\"target\":\"v2-1624555734101-1368071922922\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624555777911-8528813639702\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186022327-2899379456646\",\"sourceHandle\":\"c\",\"target\":\"v2-1624555824116-5980935004164\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624555879172-6004060017018\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186682472-9169377069812\",\"sourceHandle\":\"b\",\"target\":\"v2-1624555879399-6061041660916\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624555915156-5874967006749\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186652759-4960965729761\",\"sourceHandle\":\"b\",\"target\":\"v2-1624555994428-9110927358393\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624556067447-8263540644640\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186954765-7676883760911\",\"sourceHandle\":\"a\",\"target\":\"v2-1624556875886-7320990324971\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624557031306-3676434268333\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185980565-8743695485867\",\"sourceHandle\":\"a\",\"target\":\"v2-1624557067265-4462890990592\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624557147500-5421393999297\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186037332-6365011053297\",\"sourceHandle\":\"b\",\"target\":\"v2-1624557184209-6499014906083\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624557274697-7592685481586\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621186913293-8028595782128\",\"sourceHandle\":\"c\",\"target\":\"v2-1624557608129-9669002422247\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624557670755-2666363527596\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185871251-9919604707138\",\"sourceHandle\":\"c\",\"target\":\"v2-1624557735413-7350843822416\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624557790422-8946008787083\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}},{\"source\":\"v1-1621185911440-4962016131877\",\"sourceHandle\":\"c\",\"target\":\"v2-1624557671045-9605376381356\",\"targetHandle\":null,\"animated\":true,\"type\":\"DEFAULT_EDGE\",\"arrowHeadType\":\"arrowclosed\",\"style\":{\"stroke\":\"rgb(253 126 20)\",\"strokeWidth\":\"3px\"},\"id\":\"v2-1624557816526-4697942653801\",\"length\":6.894,\"coverageHeight\":0.6,\"data\":{\"length\":6.894,\"coverageHeight\":0.6}}],\"inputNodes\":[{\"flowTemperatureTemplate\":\"75\",\"returnTemperatureTemplate\":\"65\",\"data\":{\"label\":\"Erzeuger\"},\"position\":{\"x\":-390,\"y\":84},\"type\":\"INPUT_NODE\",\"id\":\"v1-1621177974627-4475240057456\"}],\"intermediateNodes\":[{\"label\":\"Schuetzenstr. (bis 15/26)\",\"id\":\"v1-1621185871251-9919604707138\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":-262,\"y\":302},\"data\":{\"label\":\"Schuetzenstr. (bis 15/26)\"}},{\"label\":\"Gerberstr.\",\"id\":\"v1-1621185898211-7831046798307\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":-15,\"y\":303},\"data\":{\"label\":\"Gerberstr.\"}},{\"label\":\"Schuetzenstr. (bis Kleine Rurstr.)\",\"id\":\"v1-1621185911440-4962016131877\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":-287,\"y\":542},\"data\":{\"label\":\"Schuetzenstr. (bis Kleine Rurstr.)\"}},{\"label\":\"Kleine Rurstr.\",\"id\":\"v1-1621185949072-5762848366935\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":-33,\"y\":648},\"data\":{\"label\":\"Kleine Rurstr.\"}},{\"label\":\"Grosserurstr. (Block 10)\",\"id\":\"v1-1621185960881-2278928340772\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":-263,\"y\":771},\"data\":{\"label\":\"Grosserurstr. (Block 10)\"}},{\"id\":\"v1-1621185980565-8743695485867\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":153,\"y\":132},\"data\":{\"label\":\"Schirmerstr.\"}},{\"id\":\"v1-1621185993404-8663399000808\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":134,\"y\":223},\"data\":{\"label\":\"An der Synagoge\"}},{\"id\":\"v1-1621186006012-3164455616714\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":315,\"y\":226},\"data\":{\"label\":\"Bocksgasse\"}},{\"label\":\"Gruenstr.\",\"id\":\"v1-1621186022327-2899379456646\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":161,\"y\":405.00000000000006},\"data\":{\"label\":\"Gruenstr.\"}},{\"id\":\"v1-1621186037332-6365011053297\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":297,\"y\":408},\"data\":{\"label\":\"Raderstr.\"}},{\"label\":\"Zuleitung\",\"id\":\"v1-1621186044694-1690334569077\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":-202,\"y\":128},\"data\":{\"label\":\"Zuleitung\"}},{\"id\":\"v1-1621186098528-4388784622092\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":473,\"y\":135},\"data\":{\"label\":\"Schirmerstr. 1a-9\"}},{\"label\":\"Duesseldorferstr. 23-31 (Block 3)\",\"id\":\"v1-1621186594588-2147702602155\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":425,\"y\":262},\"data\":{\"label\":\"Duesseldorferstr. 23-31 (Block 3)\"}},{\"label\":\"Duesseldorferstr. 8-21 (Block 4+6)\",\"id\":\"v1-1621186629583-9384125582204\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":416,\"y\":366},\"data\":{\"label\":\"Duesseldorferstr. 8-21 (Block 4+6)\"}},{\"label\":\"Kapuzinerstr. 1-9 (Block 4)\",\"id\":\"v1-1621186652759-4960965729761\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":775,\"y\":365},\"data\":{\"label\":\"Kapuzinerstr. 1-9 (Block 4)\"}},{\"label\":\"2-6\",\"id\":\"v1-1621186682472-9169377069812\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":177,\"y\":497},\"data\":{\"label\":\"2-6\"}},{\"label\":\"3-4\",\"id\":\"v1-1621186812935-9961997802525\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":523,\"y\":468},\"data\":{\"label\":\"3-4\"}},{\"label\":\"Koelnstr. 1-13\",\"id\":\"v1-1621186851560-6291085778803\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":695,\"y\":468},\"data\":{\"label\":\"Koelnstr. 1-13\"}},{\"label\":\"Koelnstr. 14-19\",\"id\":\"v1-1621186901941-8243265721583\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":854,\"y\":557},\"data\":{\"label\":\"Koelnstr. 14-19\"}},{\"label\":\"Stiftherrenstr.\",\"id\":\"v1-1621186913293-8028595782128\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":707,\"y\":696},\"data\":{\"label\":\"Stiftherrenstr.\"}},{\"label\":\"Marktstr.\",\"id\":\"v1-1621186926888-1586477936382\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":506,\"y\":629},\"data\":{\"label\":\"Marktstr.\"}},{\"label\":\"Grosse Rurstr. (Block 8+11)\",\"id\":\"v1-1621186938005-8206520472260\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":445,\"y\":790},\"data\":{\"label\":\"Grosse Rurstr. (Block 8+11)\"}},{\"label\":\"Schossstr. 2-8 (Block 4)\",\"id\":\"v1-1621186954765-7676883760911\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":1044,\"y\":335},\"data\":{\"label\":\"Schossstr. 2-8 (Block 4)\"}},{\"label\":\"Baierstr. 6-15\",\"id\":\"v1-1621187146185-4822434399630\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":1092,\"y\":429},\"data\":{\"label\":\"Baierstr. 6-15\"}},{\"label\":\"Kapuzinerstr. 10-17 (Block 5+9)\",\"id\":\"v1-1621187165108-3673108523208\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":1233,\"y\":421},\"data\":{\"label\":\"Kapuzinerstr. 10-17 (Block 5+9)\"}},{\"label\":\"Baierstr. 2-4\",\"id\":\"v1-1621187186495-8646703581612\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":1089,\"y\":614},\"data\":{\"label\":\"Baierstr. 2-4\"}},{\"label\":\"Schlossstr. 10-16 (Block 5)\",\"id\":\"v1-1621187204200-7802240284225\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":1441,\"y\":189},\"data\":{\"label\":\"Schlossstr. 10-16 (Block 5)\"}},{\"label\":\"Schlossstr. 18-22 (Block 9)\",\"id\":\"v1-1621187356851-6725650088354\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":1507,\"y\":465},\"data\":{\"label\":\"Schlossstr. 18-22 (Block 9)\"}},{\"label\":\"Poststr.\",\"id\":\"v1-1621187373900-1465702534212\",\"type\":\"INTERMEDIATE_NODE\",\"position\":{\"x\":1542,\"y\":659},\"data\":{\"label\":\"Poststr.\"}}],\"outputNodes\":[{\"label\":\"An der Synagoge Entnahme\",\"thermalEnergyDemand\":116531.2362,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":7,\"id\":\"v2-1624553358396-8564871909034\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":-94,\"y\":255},\"data\":{\"label\":\"An der Synagoge Entnahme\",\"thermalEnergyDemand\":116531.2362,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":7}},{\"thermalEnergyDemand\":172832.899,\"pressureLoss\":0.56,\"data\":{\"label\":\"Poststr. Entnahmestelle\"},\"position\":{\"x\":1696,\"y\":690},\"type\":\"OUTPUT_NODE\",\"id\":\"v2-1624553630050-7882764107439\",\"replicas\":10,\"loadProfileName\":\"MFH\"},{\"thermalEnergyDemand\":146073.213,\"pressureLoss\":0.56,\"data\":{\"label\":\"Stiftherrenstr. Entnahmestelle\"},\"position\":{\"x\":800,\"y\":762},\"type\":\"OUTPUT_NODE\",\"id\":\"v2-1624553709595-7616411400162\",\"replicas\":15,\"loadProfileName\":\"MFH\"},{\"thermalEnergyDemand\":142136.865,\"pressureLoss\":0.56,\"data\":{\"label\":\"Grosse Rurstr. (Block 8+11) Entnahmestelle\"},\"position\":{\"x\":270,\"y\":842},\"type\":\"OUTPUT_NODE\",\"id\":\"v2-1624553772913-9482166813274\",\"replicas\":9,\"loadProfileName\":\"MFH\"},{\"thermalEnergyDemand\":143296.576,\"pressureLoss\":0.56,\"data\":{\"label\":\"Koelnstr. 14-19 Entnahmestelle\"},\"position\":{\"x\":851,\"y\":609},\"type\":\"OUTPUT_NODE\",\"id\":\"v2-1624553908443-4220240968406\",\"replicas\":3,\"loadProfileName\":\"MFH\"},{\"thermalEnergyDemand\":168113.421,\"pressureLoss\":0.56,\"data\":{\"label\":\"Koelnstr. 1-13 Entnahmestelle\"},\"position\":{\"x\":556,\"y\":534},\"type\":\"OUTPUT_NODE\",\"id\":\"v2-1624554002738-6790837644495\",\"replicas\":16,\"loadProfileName\":\"MFH\"},{\"thermalEnergyDemand\":175657.333,\"pressureLoss\":0.56,\"data\":{\"label\":\"Schlossstr. 18-22 (Block 9) Entnahmestelle\"},\"position\":{\"x\":1606,\"y\":548},\"type\":\"OUTPUT_NODE\",\"id\":\"v2-1624554212687-9413772475276\",\"replicas\":2,\"loadProfileName\":\"MFH\"},{\"label\":\"Baierstr. 2-4 Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":106224.72,\"pressureLoss\":0.56,\"replicas\":4,\"id\":\"v2-1624554290529-4405086332189\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":1127,\"y\":680},\"data\":{\"label\":\"Baierstr. 2-4 Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":106224.72,\"pressureLoss\":0.56,\"replicas\":4}},{\"label\":\"Kapuzinerstr. 10-17 (Block 5+9) Entnahmestelle\",\"thermalEnergyDemand\":147200.412,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":8,\"id\":\"v2-1624554398898-1650076799809\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":1178,\"y\":486.00000000000006},\"data\":{\"label\":\"Kapuzinerstr. 10-17 (Block 5+9) Entnahmestelle\",\"thermalEnergyDemand\":147200.412,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":8}},{\"label\":\"Schlossstr. 10-16 (Block 5) Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":122139.26,\"pressureLoss\":0.56,\"replicas\":4,\"id\":\"v2-1624554480573-8589577025883\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":1519,\"y\":256},\"data\":{\"label\":\"Schlossstr. 10-16 (Block 5) Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":122139.26,\"pressureLoss\":0.56,\"replicas\":4}},{\"label\":\"Baierstr. 6-15 Entnahmestelle\",\"thermalEnergyDemand\":84620.6264,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":9,\"id\":\"v2-1624554544167-9319423815125\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":932,\"y\":477},\"data\":{\"label\":\"Baierstr. 6-15 Entnahmestelle\",\"thermalEnergyDemand\":84620.6264,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":9}},{\"thermalEnergyDemand\":95674.7793,\"pressureLoss\":0.56,\"data\":{\"label\":\"Bocksgasse Entnahmestelle\"},\"position\":{\"x\":216,\"y\":291},\"type\":\"OUTPUT_NODE\",\"id\":\"v2-1624554616858-7079921986001\",\"replicas\":8,\"loadProfileName\":\"MFH\"},{\"label\":\"Duesseldorferstr. 23-31 (Block 3) Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":145661.2,\"pressureLoss\":0.56,\"replicas\":5,\"id\":\"v2-1624554676177-8969630706750\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":486,\"y\":314},\"data\":{\"label\":\"Duesseldorferstr. 23-31 (Block 3) Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":145661.2,\"pressureLoss\":0.56,\"replicas\":5}},{\"label\":\"Schirmerstr. 1a-9 Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":105502.22,\"pressureLoss\":0.56,\"replicas\":6,\"id\":\"v2-1624554737173-3133658914524\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":537,\"y\":178},\"data\":{\"label\":\"Schirmerstr. 1a-9 Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":105502.22,\"pressureLoss\":0.56,\"replicas\":6}},{\"label\":\"Marktstr. Entnahmestelle\",\"thermalEnergyDemand\":201526.672,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":12,\"id\":\"v2-1624554827125-1938007856639\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":353,\"y\":688},\"data\":{\"label\":\"Marktstr. Entnahmestelle\",\"thermalEnergyDemand\":201526.672,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":12}},{\"label\":\"Grosserurstr. (Block 10) Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":227114.06,\"pressureLoss\":0.56,\"replicas\":9,\"id\":\"v2-1624554963380-7324701238204\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":-292,\"y\":831},\"data\":{\"label\":\"Grosserurstr. (Block 10) Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":227114.06,\"pressureLoss\":0.56,\"replicas\":9}},{\"thermalEnergyDemand\":127018.574,\"pressureLoss\":0.56,\"data\":{\"label\":\"Kleine Rurstr. Entnahmestelle\"},\"position\":{\"x\":-50,\"y\":730},\"type\":\"OUTPUT_NODE\",\"id\":\"v2-1624555094822-9587982611406\",\"replicas\":23,\"loadProfileName\":\"MFH\"},{\"thermalEnergyDemand\":125222.53,\"pressureLoss\":0.56,\"data\":{\"label\":\"Duesseldorferstr. 8-21 (Block 4+6) Entnahmestelle\"},\"position\":{\"x\":400.00000000000006,\"y\":414},\"type\":\"OUTPUT_NODE\",\"id\":\"v2-1624555562187-8648690458487\",\"replicas\":11,\"loadProfileName\":\"MFH\"},{\"label\":\"3-4  Entnahmestelle\",\"thermalEnergyDemand\":161987.192,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":18,\"id\":\"v2-1624555690684-7538945303035\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":394,\"y\":515},\"data\":{\"label\":\"3-4  Entnahmestelle\",\"thermalEnergyDemand\":161987.192,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":18}},{\"label\":\"Gerberstr. Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":129394.863,\"pressureLoss\":0.56,\"replicas\":3,\"id\":\"v2-1624555734101-1368071922922\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":-113,\"y\":376},\"data\":{\"label\":\"Gerberstr. Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":129394.863,\"pressureLoss\":0.56,\"replicas\":3}},{\"thermalEnergyDemand\":114750.268,\"pressureLoss\":0.56,\"data\":{\"label\":\"Gruenstr. Entnahmestelle\"},\"position\":{\"x\":-30,\"y\":440},\"type\":\"OUTPUT_NODE\",\"id\":\"v2-1624555824116-5980935004164\",\"replicas\":12,\"loadProfileName\":\"MFH\"},{\"thermalEnergyDemand\":126062.795,\"pressureLoss\":0.56,\"data\":{\"label\":\"2-6 Entnahmestelle\"},\"position\":{\"x\":123,\"y\":578},\"type\":\"OUTPUT_NODE\",\"id\":\"v2-1624555879399-6061041660916\",\"replicas\":13,\"loadProfileName\":\"MFH\"},{\"label\":\"Kapuzinerstr. 1-9 (Block 4) Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":137093.418,\"pressureLoss\":0.56,\"replicas\":5,\"id\":\"v2-1624555994428-9110927358393\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":792,\"y\":419},\"data\":{\"label\":\"Kapuzinerstr. 1-9 (Block 4) Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":137093.418,\"pressureLoss\":0.56,\"replicas\":5}},{\"label\":\"Schossstr. 2-8 (Block 4) Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":109931.723,\"pressureLoss\":0.56,\"replicas\":6,\"id\":\"v2-1624556875886-7320990324971\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":1065,\"y\":153},\"data\":{\"label\":\"Schossstr. 2-8 (Block 4) Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":109931.723,\"pressureLoss\":0.56,\"replicas\":6}},{\"label\":\"Schirmerstr. Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":491102.467,\"pressureLoss\":0.56,\"replicas\":1,\"id\":\"v2-1624557067265-4462890990592\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":143,\"y\":2},\"data\":{\"label\":\"Schirmerstr. Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":491102.467,\"pressureLoss\":0.56,\"replicas\":1}},{\"label\":\"Raderstr. Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":92039.355,\"pressureLoss\":0.56,\"replicas\":16,\"id\":\"v2-1624557184209-6499014906083\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":232,\"y\":470.00000000000006},\"data\":{\"label\":\"Raderstr. Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":92039.355,\"pressureLoss\":0.56,\"replicas\":16}},{\"label\":\"Kirche Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":336468.14,\"pressureLoss\":0.56,\"replicas\":3,\"id\":\"v2-1624557608129-9669002422247\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":605,\"y\":747},\"data\":{\"label\":\"Kirche Entnahmestelle\",\"loadProfileName\":\"MFH\",\"thermalEnergyDemand\":336468.14,\"pressureLoss\":0.56,\"replicas\":3}},{\"label\":\"Schuetzenstr. (bis Kleine Rurstr.) Entnahmestelle\",\"thermalEnergyDemand\":101813.745,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":12,\"id\":\"v2-1624557671045-9605376381356\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":-542,\"y\":584},\"data\":{\"label\":\"Schuetzenstr. (bis Kleine Rurstr.) Entnahmestelle\",\"thermalEnergyDemand\":101813.745,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":12}},{\"label\":\"Schuetzenstr. (bis 15/26) Entnahmestelle\",\"thermalEnergyDemand\":156876.331,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":19,\"id\":\"v2-1624557735413-7350843822416\",\"type\":\"OUTPUT_NODE\",\"position\":{\"x\":-515,\"y\":349},\"data\":{\"label\":\"Schuetzenstr. (bis 15/26) Entnahmestelle\",\"thermalEnergyDemand\":156876.331,\"pressureLoss\":0.56,\"loadProfileName\":\"MFH\",\"replicas\":19}}],\"temperatureSeries\":\"Gruppe A4\"}"
        val request = Gson().fromJson(jsonString, GridRequest::class.java)
        return GridService(heatDemandService, timeSeriesService).createByGridRequest(request)
    }

    private fun createStreetPipes(): List<PipeType> {
        // Quelle https://www.ingenieur.de/fachmedien/bwk/energieversorgung/dimensionierung-von-fernwaermenetzen/
        // Dämmdicke: https://www.ikz.de/uploads/media/50-55_Daemmstandards.pdf
        // Seite 91 https://www.energie-zentralschweiz.ch/fileadmin/user_upload/Downloads/Planungshilfen/Planungshandbuch_Fernwarrme_V1.0x.pdf
        return listOf(
            PipeType(0.020, 432.37, 0.04, 0.2),
            PipeType(0.025, 437.85, 0.04, 0.2),
            PipeType(0.032, 467.11, 0.04, 0.2),
            PipeType(0.040, 483.56, 0.04, 0.2),
            PipeType(0.050, 547.55, 0.04, 0.2),
            PipeType(0.065, 594.17, 0.04, 0.2),
            PipeType(0.080, 681.0, 0.04, 0.2),
            PipeType(0.100, 840.97, 0.04, 0.2),
            PipeType(0.125, 1010.08, 0.04, 0.2),
            PipeType(0.150, 1217.58, 0.04, 0.2),
            PipeType(0.200, 1449.76, 0.04, 0.2),
            PipeType(0.250, 1941.55, 0.04, 0.2),
            PipeType(0.300, 2433.33, 0.04, 0.2),
            PipeType(0.350, 2925.12, 0.04, 0.2),
            PipeType(0.400, 3416.9, 0.04, 0.2)
        )
    }

}