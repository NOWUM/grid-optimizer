package de.fhac.ewi.services

import de.fhac.ewi.dto.GridRequest
import de.fhac.ewi.dto.MassenstromResponse
import de.fhac.ewi.model.Grid
import de.fhac.ewi.util.catchParseError
import de.fhac.ewi.util.massenstrom
import de.fhac.ewi.util.repeatEach
import de.fhac.ewi.util.toDoubleFunction

class GridService(
    private val demandService: HeatDemandService,
    private val temperatureService: TemperatureTimeSeriesService
) {

    fun createByGridRequest(request: GridRequest): Grid {
        val grid = Grid()

        val groundSeries = temperatureService.getSeries(request.temperatureSeries)

        request.inputNodes.forEach {
            val flowFunction =
                catchParseError("Invalid flow in formula in node ${it.id}.") { it.flowTemperatureTemplate.toDoubleFunction() }
            val returnFunction =
                catchParseError("Invalid flow out formula in node ${it.id}.") { it.returnTemperatureTemplate.toDoubleFunction() }
            grid.addInputNode(it.id, groundSeries, flowFunction, returnFunction)
        }
        request.intermediateNodes.forEach {
            grid.addIntermediateNode(it.id)
        }
        request.outputNodes.forEach {
            val curve = demandService.createCurve(it.thermalEnergyDemand, it.loadProfileName, request.temperatureSeries)
            grid.addOutputNode(it.id, curve, it.pressureLoss)
        }
        request.pipes.forEach {
            grid.addPipe(it.id, it.source, it.target, it.length, it.coverageHeight)
        }

        return grid
    }

    fun calculateMaxMassenstrom(grid: Grid, temperatureSeries: String): MassenstromResponse {
        val tempRow = temperatureService.getSeries(temperatureSeries).temperatures.repeatEach(24)
        val heatDemand = grid.input.connectedThermalEnergyDemand
        val massenstroms = tempRow.indices.map { idx ->
            massenstrom(grid.input.flowInTemperature[idx], grid.input.flowOutTemperature[idx], heatDemand[idx])
        }
        return MassenstromResponse(tempRow, grid.input.flowInTemperature, grid.input.flowOutTemperature, heatDemand.curve, massenstroms)
    }
}