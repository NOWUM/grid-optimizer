@file:Suppress("DuplicatedCode")

package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.util.maxOrElse

/**
 * Strategie dient der Senkung des maximalen Druckverlusts im Netz.
 *
 * Es wird der Druckverlust pro Meter im kritischen Pfad (nur Druckverluste in Rohrleitung) bestimmt.
 * Alle Rohrleitungen die pro Meter einen höheren Druckverlust aufweisen, sind für die Optimierung interessant.
 * Es werden die 10 Rohrleitungen mit dem größten Faktor ausgewählt.
 * Es wird für jede Pipe der aktuell beste Durchmesser & der Durchmesser eine Nummer größer ausprobiert und dann jeweils die Kosten bestimmt.
 *
 * Large: Ja
 * Medium: Nein
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
                lowerPressureLossFound = optimizePipes(pipes, skipSmallerThenCurrent = true, maxDifferenceToCurrent = 1)
            } while (lowerPressureLossFound)
        }
    }
}