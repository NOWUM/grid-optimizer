@file:Suppress("DuplicatedCode")

package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Node
import de.fhac.ewi.model.Optimizer

/**
 * Strategie dient der Senkung des maximalen Druckverlusts im Netz.
 *
 * Dazu wird für jeden Knoten mit angeschlossenen Pipes eben diese angeschlossenen Pipes + der Pfad zum Einspeisepunkt zeitgleich untersucht.
 * Es wird für jede Pipe der aktuell beste Durchmesser & der Durchmesser eine Nummer größer ausprobiert und dann jeweils die Kosten bestimmt.
 *
 * Large: Nein
 * Medium: Nein
 */
object LowerPressureLoss : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        checkNode(grid.input)
    }

    private fun Optimizer.checkNode(node: Node) {
        node.connectedChildNodes.forEach { child ->
            if (child.connectedChildPipes.isNotEmpty())
                checkNode(child)
        }

        optimizePipes(node.connectedChildPipes + node.pathToSource, skipSmallerThenCurrent=true, maxDifferenceToCurrent = 1)
    }
}