package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.OutputNode
import de.fhac.ewi.model.Pipe

/**
 * Vergrößert alle Pipes um eins. Damit wird der maximale Druckverlust in jedem Fall reduziert.
 */
object IncreaseAllPipesByOne : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        grid.pipes.forEach { pipe ->
            val index = investParams.pipeTypes.indexOfFirst { it == pipe.type }
            pipe.type = investParams.pipeTypes[index+1]
        }
        gridCosts = investParams.calculateCosts(grid)
    }
}