package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.Pipe

/**
 * Strategie dient der Optimierung sämtlicher Leitungen im Netz.
 *
 * Es ist eine Kopie von [LayerDownToOneByOne] und soll eigentlich die Anzahl der Checks
 * durch eine kluge Auswahl der zu überprüfenden Rohre reduzieren.
 *
 * Das Wort "eigentlich" beschreibt den Erfolg dieser Strategie schon ganz gut.
 */
object WiseSelector : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        val pipesToCheck = grid.pipes.sortedBy { it.source.pathToSource.size }.toMutableList()

        while (pipesToCheck.isNotEmpty()) {
            val pipe = pipesToCheck.removeFirst()
            if (optimizePipe(pipe, skipSmallerThenCurrent = true)) {
                pipe.target.connectedChildPipes.filterNot { it in pipesToCheck }.forEach(pipesToCheck::add)
                pipe.source.connectedChildPipes.filterNot { it in pipesToCheck }.forEach(pipesToCheck::add)
                pipe.source.pathToSource.filterNot { it in pipesToCheck }.reversed().forEach(pipesToCheck::add)
            }
        }
    }
}