package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer

/**
 * Optimiert nur die Rohrleitungen auf dem kritischen Pfad.
 * Alle Rohrleitungen werden zeitgleich aktualisiert.
 */
object CriticalPathAsOne : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val pipes = grid.criticalPath.toList()
        optimizePipePath(pipes)
    }
}