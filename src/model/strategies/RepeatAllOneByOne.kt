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
object RepeatAllOneByOne : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        // TODO sortedByDescending ist in Medium Grid besser
        val pipes = grid.nodes.filterIsInstance<OutputNode>().sortedBy { it.pathToSource.sumOf(Pipe::length) }.flatMap { it.pathToSource.toList().reversed() }.distinct()
        var anyPipeUpdated: Boolean
        do {
            anyPipeUpdated = false
            for (pipe in pipes) {
                if (optimizePipe(pipe, fastMode = true))
                    anyPipeUpdated = true
            }
        } while (anyPipeUpdated)
    }
}