package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.OutputNode
import de.fhac.ewi.model.Pipe

/**
 * TODO
 */
object PathToSourceOneByOneFast : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        var anyPathUpdated: Boolean
        do {
            anyPathUpdated = false
            grid.nodes.filterIsInstance<OutputNode>().map { it.pathToSource }.forEach { pipes ->
                val pipe = pipes.first()
                if (optimizeFast(pipe))
                    anyPathUpdated = true
            }
        } while (anyPathUpdated)
    }

    private fun Optimizer.optimizeFast(pipe: Pipe): Boolean {
        if (optimizePipe(pipe)) {

            for (parent in pipe.source.pathToSource) {
                if (!optimizePipe(parent))
                    break // Skip updating parent path, if no update was made
            }
            return true
        }
        return false
    }
}