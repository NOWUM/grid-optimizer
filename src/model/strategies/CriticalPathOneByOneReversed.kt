package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer

/**
 * Optimiert nur die Rohrleitungen auf dem kritischen Pfad. Und zwar eine Rohrleitung nach der anderen.
 * Dies wird solange wiederholt, bis kein Update im kritischen Pfad mehr vorgenommen wird.
 */
object CriticalPathOneByOneReversed : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        val pipes = grid.criticalPath.reversed()

        for (pipe in pipes) {
            optimizePipe(pipe)
        }
    }
}