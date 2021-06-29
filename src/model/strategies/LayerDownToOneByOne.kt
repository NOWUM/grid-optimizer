package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.Pipe

/**
 * Strategie dient der Optimierung sämtlicher Leitungen im Netz.
 *
 * Alle Leitungen werden nach Abstand (in Anzahl Rohrleitungen) zum Einspeisepunkt aufsteigend sortiert.
 * (Die direkt am Einspeisepunkt angeschlossenen Leitungen werden zuerst geprüft, danach die daran angeschlossenen, usw.)
 *
 * Für jede Leitung werden alle sinnvollen Kombinationen an Rohrdurchmessern geprüft
 * und solange mindestens ein Rohrdurchmesser geändert wurde, wird nach Abschluss die Liste nochmals abgearbeitet.
 */
object LayerDownToOneByOne : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        val orderedPipes = grid.pipes.sortedBy { it.source.pathToSource.size }
        var anyPipeUpdated: Boolean
        do {
            anyPipeUpdated = false
            for (pipe in orderedPipes)
                if (optimizePipe(pipe, skipSmallerThenCurrent = true))
                    anyPipeUpdated = true
        } while (anyPipeUpdated)
    }
}