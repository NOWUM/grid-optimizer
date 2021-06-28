@file:Suppress("DuplicatedCode")

package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.OutputNode
import de.fhac.ewi.util.maxOrElse

/**
 * Strategie dient der Senkung des maximalen Druckverlusts im Netz.
 *
 * Nimmt den Pfad mit dem höchsten Druckverlust (in Rohrleitungen) pro Meter.
 * Dieser wird für die Optimierung herangezogen. Zusätzlich werden alle Rohrleitungen mit optimiert, die Berührungspunkte zum kritischen Pfad haben.
 * Es wird für jede Pipe der aktuell beste Durchmesser & der Durchmesser eine Nummer größer ausprobiert und dann jeweils die Kosten bestimmt.
 *
 * Large: Nein
 * Medium: Nein
 */
object LowerPressureLoss5 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val highestLossPath = grid.nodes.filterIsInstance<OutputNode>()
            .maxByOrNull { n -> n.pathToSource.sumOf { it.pipePressureLoss.maxOrElse() } }!!.pathToSource
        val pipes = highestLossPath.flatMap { it.source.connectedChildPipes }
        optimizePipes(pipes, skipSmallerThenCurrent = true, maxDifferenceToCurrent = 1)

    }
}