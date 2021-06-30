package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.util.maxOrElse
import kotlin.math.max

/**
 * Look at code.
 *
 * Large: Nein
 * Medium: Nein
 */
object CrazyStrategy : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val ns = listOf(15)
        for (n in ns) {
            val criticalPressure =
                grid.mostDistantNode.pathToSource.sumOf { it.pipePressureLoss.maxOrElse() } / grid.mostDistantNode.pathToSource.sumOf { it.length }
            val criticalHeatLoss =
                grid.mostDistantNode.pathToSource.sumOf { it.heatLoss.sum() } / grid.mostDistantNode.pathToSource.sumOf { it.length }
            var betterCombinationFound: Boolean
            do {
                val pipes = grid.pipes
                    .sortedByDescending {
                        max(
                            it.pipePressureLoss.maxOrElse() / it.length / criticalPressure,
                            it.heatLoss.sum() / it.length / criticalHeatLoss
                        )
                    }
                    .take(n)
                    .sortedByDescending { it.source.pathToSource.size }
                betterCombinationFound = optimizePipes(pipes, maxDifferenceToCurrent = 1)
            } while (betterCombinationFound)
        }
    }
}