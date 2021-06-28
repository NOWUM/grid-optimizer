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
 * Large: ?
 * Medium: ?
 */
object LowerPressureLoss8 : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val pipesToOptimize = mutableListOf<Pipe>()
        grid.pipes.groupBy { it.source.pathToSource.size }.forEach { (_, pipes) ->
            val highestPressureLoss = pipes.maxOf { it.target.pathToSource.maxOf { p -> p.pipePressureLoss.maxOrElse() } }
            val possible = pipes.filter { it.target.pathToSource.maxOf { p -> p.pipePressureLoss.maxOrElse() } / highestPressureLoss > 0.80 }
            // Nur nehmen, wenn das weniger als 50 % der Pipes oder 2 Stück sind
            if (possible.size / pipes.size < 0.5 || possible.size <= 3)
                pipesToOptimize += possible
        }
        println("Optimizing ${pipesToOptimize.size} as one...")
        optimizePipes(pipesToOptimize.reversed(), skipSmallerThenCurrent = true, maxDifferenceToCurrent = 1)
    }
}