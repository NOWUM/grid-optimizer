@file:Suppress("DuplicatedCode")

package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.Pipe
import de.fhac.ewi.util.maxOrElse

/**
 * Strategie dient der Senkung des maximalen Druckverlusts im Netz.
 *
 * Die Rohrleitungen werden nach "Ebenen" (Entfernung zum Einspeisepunkt in Anzahl Rohrleitungen) sortiert.
 * Für jede dieser Ebenen wird der maximale gesamte Druckverlust bestimmt.
 * Alle Pipes die mindestens 60 % genau dieses gesamten Druckverlusts haben, werden für die Optimierung ausgewählt.
 * Es wird für jede Pipe der aktuell beste Durchmesser & der Durchmesser eine Nummer größer ausprobiert und dann jeweils die Kosten bestimmt.
 *
 * Large: Ja
 * Medium: Nein
 *
 * TODO Ggf in Abhängigkeit der Länge?
 * TODO Ggf in pipePressureLoss
 */
object LowerPressureLoss2 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val possibleToHigh = mutableListOf<Pipe>()
        grid.pipes.groupBy { it.source.pathToSource.size }.forEach { (_, pipes) ->
            val highest = pipes.maxOf { it.totalPressureLoss.maxOrElse() }
            possibleToHigh += pipes.filter { it.totalPressureLoss.maxOrElse() / highest > 0.60 }
        }
        optimizePipes(possibleToHigh.reversed(), skipSmallerThenCurrent = true, maxDifferenceToCurrent = 1)
    }
}