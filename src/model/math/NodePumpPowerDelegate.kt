package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.PipeBasedCalculableDelegate
import de.fhac.ewi.util.subscribeIfChanged

/**
 * ### Benötigte Pumpleistung in einem Knoten
 * ... ist die höchste benötigte Pumpleistung aller angeschlossenen Rohrleitungen.
 *
 * @param T - For Delegate
 */
class NodePumpPowerDelegate<T>: PipeBasedCalculableDelegate<T>() {

    override fun recalculateIndexed(index: Int, pipes: List<Pipe>): Double {
        return pipes.maxOf { it.totalPumpPower[index] }
    }

    override fun onPipeConnect(pipe: Pipe) {
        pipe::totalPumpPower.subscribeIfChanged(this::updateValue)
    }

}