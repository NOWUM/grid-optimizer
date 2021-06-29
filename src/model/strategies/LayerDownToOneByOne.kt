package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.Pipe

/**
 * TODO
 */
object LayerDownToOneByOne : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        val orderedPipes = grid.pipes.sortedBy { it.source.pathToSource.size }
        var anyPipeUpdated: Boolean
        do {
            anyPipeUpdated = false
            for (pipe in orderedPipes)
                if (optimizePipe(pipe, skipSmallerThenCurrent = true))
                    anyPipeUpdated = true
        } while (anyPipeUpdated)
    }
}