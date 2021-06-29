package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer

/**
 * Strategie dient zur Bestimmung der optimalen Rohrdurchmesser auf dem längsten Pfad.
 *
 * Die Leitungen auf dem längste Pfad werden von Einspeisepunkt bis hin zur Entnahmestelle nacheinander optimiert.
 */
object CriticalPathOneByOneReversed : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        val pipes = grid.criticalPath.reversed()

        for (pipe in pipes) {
            optimizePipe(pipe)
        }
    }
}