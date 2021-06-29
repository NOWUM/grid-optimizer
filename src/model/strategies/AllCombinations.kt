package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.OutputNode
import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.PipeType

/**
 * Überprüft alle möglichen Kombinationen.
 * Dazu werden die Rohre nach ihrer Distanz zum Einspeisepunkt absteigend sortiert und jede sinnvolle Kombination getestet.
 * Sinnvoll meint hierbei, dass der Rohrdurchmesser mindestens genauso groß sein muss, wie der größte daran angeschlossene Rohrdurchmesser.
 */
object AllCombinations : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        grid.pipes.forEach { it.type = investParams.pipeTypes.first() }
        optimizePipes(grid.pipes.sortedByDescending { it.target.pathToSource.size })
    }
}