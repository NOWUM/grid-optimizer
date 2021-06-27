package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.InputNode
import de.fhac.ewi.model.Node
import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.Pipe

/**
 * Diese Strategie versucht den maximalen Druckverlust im Netz zu senken, indem die "schlimmsten" Leitungen zeitgleich optimiert werden
 */
object LowerPressureLoss3 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val ns = listOf(9)
        for (n in ns) {
            var lowerPressureLossFound: Boolean
            do {
                val pipes = grid.pipes
                    .sortedByDescending {
                        (it.pipePressureLoss.maxOrNull() ?: 0.0) / it.length / (it.target.pressureLoss.maxOrNull()
                            ?: 0.0)
                    }
                    .take(n)
                    .sortedByDescending { it.source.pathToSource.size }
                lowerPressureLossFound = optimizePipes(pipes, skipSmallerThenCurrent = true, maxTries = 2)
            } while (lowerPressureLossFound)
        }
    }
}