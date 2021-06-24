package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.OutputNode

/**
 * Optimiert alle Pfade von Entnahmestelle zum Einspeisepunkt.
 * Es wird der gesamte Pfad auf einmal optimiert.
 */
object PathToSourceAsOne : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        var anyPathUpdated: Boolean
        do {
            anyPathUpdated = false
            grid.nodes.filterIsInstance<OutputNode>().map { it.pathToSource.toList() }.forEach { pipes ->
                if (optimizePipePath(pipes))
                    anyPathUpdated = true
            }
        } while (anyPathUpdated)
    }
}