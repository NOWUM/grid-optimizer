package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.PipeBasedCalculableDelegate
import de.fhac.ewi.util.subscribeIfChanged

/**
 *
 * TODO Kommentar neu generieren
 */
class NodePressureLossDelegate<T>: PipeBasedCalculableDelegate<T>() {

    override fun recalculateIndexed(index: Int, pipes: List<Pipe>): Double {
        return pipes.maxOf { it.totalPressureLoss[index] }
    }

    override fun onPipeConnect(pipe: Pipe) {
        pipe::totalPressureLoss.subscribeIfChanged(this::updateValue)
    }

}