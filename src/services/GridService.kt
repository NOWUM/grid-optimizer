package de.fhac.ewi.services

import de.fhac.ewi.dto.GridRequest
import de.fhac.ewi.model.Grid
import de.fhac.ewi.util.toDoubleFunction

class GridService {

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
            grid.addOutputNode(it.id, it.thermalEnergyDemand, it.pressureLoss)
        }
        request.pipes.forEach {
            grid.addPipe(it.id, it.source, it.target, it.length)
        }

        return grid
    }
}