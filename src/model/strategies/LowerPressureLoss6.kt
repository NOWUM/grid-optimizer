@file:Suppress("DuplicatedCode")

package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.util.maxOrElse

/**
 * Strategie dient der Senkung des maximalen Druckverlusts im Netz.
 *
 * Ähnlich zu [LowerPressureLoss4].
 * Es wird der Druckverlust im kritischen Pfad (nur Druckverluste in Rohrleitung) bestimmt und durch die Anzahl der Rohrleitungen geteilt.
 * Alle Rohrleitungen die einen höheren Druckverlust aufweisen, sind für die Optimierung interessant.
 * Es werden die 10 Rohrleitungen mit dem größten Faktor ausgewählt.
 * Es wird für jede Pipe der aktuell beste Durchmesser & der Durchmesser eine Nummer größer ausprobiert und dann jeweils die Kosten bestimmt.
 *
 * Large: Nein
 * Medium: Nein
 */
object LowerPressureLoss6 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val ns = listOf(10)
        for (n in ns) {
            val criticalPressure = grid.mostDistantNode.maxPressureLossInPath / grid.mostDistantNode.pathToSource.size
            var lowerPressureLossFound: Boolean
            do {
                val pipes = grid.pipes
                    .sortedByDescending { it.pipePressureLoss.maxOrElse() / criticalPressure }
                    .take(n)
                    .sortedByDescending { it.source.pathToSource.size }
                lowerPressureLossFound = optimizePipes(pipes, skipSmallerThenCurrent = true, maxDifferenceToCurrent = 1)
            } while (lowerPressureLossFound)
        }
    }
}