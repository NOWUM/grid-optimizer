package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.OutputNode

/**
 * Optimiert alle Pfade von Entnahmestelle zum Einspeisepunkt.
 * Es wird jede Rohrleitung einzeln untersucht.
 */
object PathToSourceOneByOne : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        var anyPathUpdated: Boolean
        do {
            anyPathUpdated = false
            grid.nodes.filterIsInstance<OutputNode>().map { it.pathToSource }.forEach { pipes ->
                for (pipe in pipes) {
                    if (optimizePipe(pipe))
                        anyPathUpdated = true
                }
            }
        } while (anyPathUpdated)
    }
}