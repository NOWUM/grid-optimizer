@file:Suppress("DuplicatedCode")

package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.Pipe
import de.fhac.ewi.util.maxOrElse

/**
 * Strategie dient der Senkung des maximalen Druckverlusts im Netz.
 *
 * TODO
 * Ausnahme: Es haben mehr als 50% auf dieser Ebene genau diesen Druckverlust. Dann gehen wir davon aus, dass der Druckverlust so sein muss.
 * Es wird für jede Pipe der aktuell beste Durchmesser & der Durchmesser eine Nummer größer ausprobiert und dann jeweils die Kosten bestimmt.
 *
 * Large: ?
 * Medium: ?
 */
object LowerPressureLoss10 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        while (grid.mostDistantNode.maxPressureLossInPath / grid.mostDistantNode.pathToSource.sumOf(Pipe::length) >= 0.003) {

        }
    }
}