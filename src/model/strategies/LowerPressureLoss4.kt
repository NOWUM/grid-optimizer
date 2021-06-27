package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.util.maxOrElse

/**
 * Druckverlust pro Meter im kritischen Pfad bestimmen, dann gucken welche Rohre da drüber liegen -> Diese müssen vergrößert werden.
 */
object LowerPressureLoss4 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val ns = listOf(10)
        for (n in ns) {
            val criticalPressure =
                grid.criticalPath.sumOf { it.pipePressureLoss.maxOrElse() } / grid.criticalPath.sumOf { it.length }
            var lowerPressureLossFound: Boolean
            do {
                val pipes = grid.pipes
                    .sortedByDescending { it.pipePressureLoss.maxOrElse() / it.length / criticalPressure }
                    .take(n)
                    .sortedByDescending { it.source.pathToSource.size }
                lowerPressureLossFound = optimizePipes(pipes, skipSmallerThenCurrent = true, maxTries = 2)
            } while (lowerPressureLossFound)
        }
    }
}