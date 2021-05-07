package de.fhac.ewi.services

import de.fhac.ewi.dto.GridRequest
import de.fhac.ewi.dto.MassenstromResponse
import de.fhac.ewi.model.Grid
import de.fhac.ewi.util.massenstrom
import de.fhac.ewi.util.repeatEach
import de.fhac.ewi.util.toDoubleFunction

class GridService(private val demandService: HeatDemandService,
                  private val temperatureService: TemperatureTimeSeriesService) {

    fun createByGridRequest(request: GridRequest): Grid {
        val grid = Grid()

        request.inputNodes.forEach {
            val flowFunction = it.flowTemperatureTemplate.toDoubleFunction()
            val returnFunction = it.returnTemperatureTemplate.toDoubleFunction()
            grid.addInputNode(it.id, flowFunction, returnFunction)
        }
        request.intermediateNodes.forEach {
            grid.addIntermediateNode(it.id)
        }
        request.outputNodes.forEach {
            val curve = demandService.createCurve(it.thermalEnergyDemand, it.loadProfileName, request.temperatureSeries)
            grid.addOutputNode(it.id, curve, it.pressureLoss)
        }
        request.pipes.forEach {
            grid.addPipe(it.id, it.source, it.target, it.length)
        }

        return grid
    }

    fun calculateMaxMassenstrom(grid: Grid, temperatureSeries: String): MassenstromResponse {
        val tempRow = temperatureService.getSeries(temperatureSeries).temperatures.repeatEach(24)
        val flowTemps = tempRow.map { grid.input.flowTemperature(it) }
        val returnTemps = tempRow.map { grid.input.returnTemperature(it) }
        val heatDemand = grid.input.connectedThermalEnergyDemand
        val massenstroms = tempRow.indices.map { index -> massenstrom(flowTemps[index], returnTemps[index], heatDemand[index]) }
        return MassenstromResponse(tempRow, flowTemps, returnTemps, heatDemand.curve, massenstroms)
    }
}