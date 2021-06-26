package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.PipeBasedCalculableDelegate
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.updateIfNeeded

/**
 * ### Druckverlust in einem Knoten
 * ... ist der höchste Druckverlust aller angeschlossenen Rohrleitungen.
 *
 * @param T - For Delegate
 */
class NodePressureLossDelegate<T> : PipeBasedCalculableDelegate<T>() {

    override fun recalculate(pipes: List<Pipe>): DoubleArray {
        val pressureLosses = pipes.map { it.totalPressureLoss }
        return DoubleArray(8760) {index -> pressureLosses.maxOf { it[index] } }
    }

    override fun onPipeConnect(pipe: Pipe) {
        pipe::totalPressureLoss.subscribeIfChanged(this)
    }

    override fun onPossiblePipeUpdate(pipe: Pipe) {
        pipe::totalPressureLoss.updateIfNeeded() // on change it will trigger recalculation of this property
    }
}