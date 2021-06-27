package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.Pipe

/**
 * Diese Strategie versucht den maximalen Druckverlust im Netz zu senken, indem die "schlimmsten" Leitungen zeitgleich optimiert werden
 */
object LowerPressureLoss2 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val possibleToHigh = mutableListOf<Pipe>()
        grid.pipes.groupBy { it.source.pathToSource.size }.forEach { (_, pipes) ->
            val highest = pipes.maxOf { it.totalPressureLoss.maxOrNull() ?: 0.0 }
            possibleToHigh += pipes.filter { (it.totalPressureLoss.maxOrNull() ?: 0.0) / highest > 0.60 }
        }
        optimizePipes(possibleToHigh.reversed(), skipSmallerThenCurrent = true, maxTries = 2)
    }
}