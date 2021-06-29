package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.OutputNode
import de.fhac.ewi.model.Pipe

/**
 * Optimiert nur die Rohrleitungen auf dem kritischen Pfad. Und zwar eine Rohrleitung nach der anderen.
 * Dies wird solange wiederholt, bis kein Update im kritischen Pfad mehr vorgenommen wird.
 */
object CriticalPathOneByOneReversed : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        val pipes = grid.criticalPath.reversed()
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