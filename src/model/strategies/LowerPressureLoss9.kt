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
object LowerPressureLoss9 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val pipesToOptimize = mutableListOf<Pipe>()
        grid.pipes.groupBy { it.source.pathToSource.size }.forEach { (_, pipes) ->
            val highestPressureLoss = pipes.maxOf { it.target.pathToSource.sumOf { p -> p.pipePressureLoss.maxOrElse() } }
            val possible = pipes.filter { it.target.pathToSource.sumOf { p -> p.pipePressureLoss.maxOrElse() } / highestPressureLoss > 0.9 }

            // Nur nehmen, wenn das weniger als 33 % der Pipes oder 2 Stück sind
            if (possible.size / pipes.size < 0.3 || possible.size <= 1)
                pipesToOptimize += possible
        }

        println("Optimize ${pipesToOptimize.size} as one...")
        optimizePipes(pipesToOptimize.sortedByDescending { it.source.pathToSource.size }, skipSmallerThenCurrent = true, maxDifferenceToCurrent = 1)
    }
}