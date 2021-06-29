@file:Suppress("DuplicatedCode")

package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.util.maxOrElse

/**
 * Strategie dient der Senkung des maximalen Druckverlusts im Netz.
 *
 * Die Pipes die einen hohen Druckverlust pro Meter im Vergleich zu dem Druckverlust des daran angeschlossenen Knotens haben, sind interessant.
 * Es werden die 9 Pipes mit dem größten Faktor ausgewählt.
 * Es wird für jede Pipe der aktuell beste Durchmesser & der Durchmesser eine Nummer größer ausprobiert und dann jeweils die Kosten bestimmt.
 *
 * Large: Ja
 * Medium: Nein
 */
object LowerPressureLoss3 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val ns = listOf(9)
        for (n in ns) {
            var lowerPressureLossFound: Boolean
            do {
                val pipes = grid.pipes
                    .sortedByDescending {
                        it.pipePressureLoss.maxOrElse() / it.length / it.target.pressureLoss.maxOrElse()
                    }
                    .take(n)
                    .sortedByDescending { it.source.pathToSource.size }
                lowerPressureLossFound = optimizePipes(pipes, skipSmallerThenCurrent = true, maxDifferenceToCurrent = 1)
            } while (lowerPressureLossFound)
        }
    }
}