@file:Suppress("DuplicatedCode")

package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.OutputNode
import de.fhac.ewi.model.Pipe
import de.fhac.ewi.util.maxOrElse

/**
 * Strategie 10. Zur Verminderung des Druckverlusts im gesamten Netz.
 *
 * Es wird davon ausgegangen, dass der Druckverlust im längsten Pfad nicht weiter optimiert werden kann.
 * Alle Pfade, die einen höheren Druckverlust haben, sind für die Optimierung interessant.
 */
object LowerPressureLoss10 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val longestPathLoss = grid.mostDistantNode.maxPressureLossInPath
        val possiblePipes = grid.nodes.filterIsInstance<OutputNode>()
            .filter { it.maxPressureLossInPath >= longestPathLoss }
            .flatMap { it.pathToSource.toList() }
            .apply { println("> There are $size paths with higher or equal pressure loss then to most distant node.") }
            .distinct()
            .sortedByDescending { it.pipePressureLoss.maxOrElse() }
            .apply { println("> There are $size pipes to possible optimize. Taking a maximum of 20 due to performance.") }
            .take(20)
            .sortedByDescending { it.source.pathToSource.size }

        optimizePipes(possiblePipes, maxDifferenceToCurrent = 2, skipSmallerThenCurrent = true)
    }
}