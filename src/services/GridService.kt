package de.fhac.ewi.services

import de.fhac.ewi.dto.GridRequest
import de.fhac.ewi.model.Grid

class GridService {

    fun createByGridRequest(request: GridRequest): Grid {
        val grid = Grid()
        request.inputNodes.forEach { grid.addInputNode(it.id) }
        request.intermediateNodes.forEach { grid.addIntermediateNode(it.id) }
        request.outputNodes.forEach { grid.addOutputNode(it.id, 42.0, 0.5) }
        request.pipes.forEach { grid.addPipe(it.id, it.source, it.target, it.length) }
        return grid
    }
}