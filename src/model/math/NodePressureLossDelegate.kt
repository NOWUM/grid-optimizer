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

    override fun recalculateIndexed(index: Int, pipes: List<Pipe>): Double {
        return pipes.maxOfOrNull { it.totalPressureLoss[index] }?:0.0 // Falls keine Pipe angeschlossen ist, gibt es keinen Druckverlust.
    }

    override fun onPipeConnect(pipe: Pipe) {
        pipe::totalPressureLoss.subscribeIfChanged(this)
    }

    override fun onPossiblePipeUpdate(pipe: Pipe) {
        pipe::totalPressureLoss.updateIfNeeded() // on change it will trigger recalculation of this property
    }
}