package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer

/**
 * Diese Strategie versucht den maximalen Druckverlust im Netz zu senken, indem die "schlimmsten" Leitungen zeitgleich optimiert werden
 */
object LowerHeatLoss : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val ns = listOf(6)
        for (n in ns) {
            var lowerHeatLossFound: Boolean
            do {
                val pipes = grid.pipes
                    .sortedByDescending {
                        it.heatLoss.sum() / it.length
                    }
                    .take(n)
                    .sortedByDescending { it.source.pathToSource.size }
                lowerHeatLossFound = optimizePipes(pipes, skipBiggerThenCurrent = true)
            } while (lowerHeatLossFound)
        }
    }
}