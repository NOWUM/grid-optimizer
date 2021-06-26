package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.OutputNode
import de.fhac.ewi.model.Pipe

/**
 * Überprüft alle Rohre im Netz.
 * Für jedes Rohr werden alle möglichen Rohrtypen eingebaut und die Kosten untersucht.
 * Sollte bei irgendeinem Rohr ein Update gefunden werden, werden alle Rohre nochmal geprüft.
 *
 * Der Algorithmus endet, wenn alle Rohre im Netz einmal geprüft wurden, ohne das ein besserer Rohrtyp gefunden wurde.
 */
object V3Strategy : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        val pipes = grid.nodes.filterIsInstance<OutputNode>().sortedByDescending { it.pathToSource.sumOf(Pipe::length) }.flatMap { it.pathToSource.toList().reversed() }.distinct()
        var anyPipeUpdated: Boolean
        do {
            anyPipeUpdated = false
            for (pipe in pipes) {
                if (optimizePipe(pipe)) {
                    anyPipeUpdated = true
                    // Wenn ein Update gefunden wurde, nochmal fix den Pfad zum Input Node (Pipe für Pipe) aktualisieren
                    for (parent in pipe.source.pathToSource)
                        optimizePipe(pipe)
                }
            }
        } while (anyPipeUpdated)
    }
}