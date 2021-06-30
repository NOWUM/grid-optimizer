@file:Suppress("DuplicatedCode")

package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.util.maxOrElse

/**
 * Strategie dient der Senkung des maximalen Druckverlusts im Netz.
 *
 * Ähnlich zu [LowerPressureLoss3]
 * Die Pipes die einen hohen Druckverlust pro Meter im Vergleich zu des daran angeschlossenen Wärmebedarfs sind interessant.
 * Es werden die 9 Pipes mit dem größten Faktor ausgewählt.
 * Es wird für jede Pipe der aktuell beste Durchmesser & der Durchmesser eine Nummer größer ausprobiert und dann jeweils die Kosten bestimmt.
 *
 * Large: Ja
 * Medium: Nein
 */
object LowerPressureLoss7 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val ns = listOf(10)
        for (n in ns) {
            var lowerPressureLossFound: Boolean
            do {
                val pipes = grid.pipes
                    .sortedByDescending { it.pipePressureLoss.maxOrElse() / it.length / it.target.energyDemand.maxOrElse() }
                    .take(n)
                    .sortedByDescending { it.source.pathToSource.size }
                lowerPressureLossFound = optimizePipes(pipes, skipSmallerThenCurrent = true, maxDifferenceToCurrent = 1)
            } while (lowerPressureLossFound)
        }
    }
}