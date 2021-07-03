package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer

/**
 * Optimiert nur die Rohrleitungen auf dem längsten Pfad. Und zwar eine Rohrleitung nach der anderen.
 * Dies wird solange wiederholt, bis kein Update im kritischen Pfad mehr vorgenommen wird.
 */
object LongestPathOneByOne : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        val pipes = grid.mostDistantNode.pathToSource
        var anyPipeUpdated: Boolean
        do {
            anyPipeUpdated = false
            for (pipe in pipes) {
                if (optimizePipe(pipe))
                    anyPipeUpdated = true
            }
        } while (anyPipeUpdated)
    }
}