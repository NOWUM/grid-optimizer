package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer

/**
 * Optimiert nur die Rohrleitungen auf dem längsten Pfad.
 * Alle Rohrleitungen werden zeitgleich aktualisiert.
 */
object LongestPathAsOne : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val pipes = grid.mostDistantNode.pathToSource.toList()
        optimizePipes(pipes)
    }
}