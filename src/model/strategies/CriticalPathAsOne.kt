package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer

/**
 * Optimiert nur die Rohrleitungen auf dem kritischen Pfad.
 * Alle Rohrleitungen werden zeitgleich aktualisiert.
 * Dies wird solange wiederholt, bis kein Update im kritischen Pfad mehr vorgenommen wurde.
 */
object CriticalPathAsOne : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        val pipes = grid.criticalPath.toList()
        var anyUpdatesOnPath: Boolean
        do {
            anyUpdatesOnPath = false
            if (optimizePipePath(pipes))
                anyUpdatesOnPath  = true
        } while (anyUpdatesOnPath)
    }
}