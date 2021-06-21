package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.PipeBasedCalculableDelegate
import de.fhac.ewi.util.subscribeIfChanged

/**
 * ### Energiebedarf in einem Knoten
 * ... setzt sich aus der Summe des Energiebedarfs aller angeschlossenen Rohre zusammen.
 *
 * Besonderheit: Bei einem OutputNode ist der Energiebedarf der zuvor angegeben wurde.
 *
 * @param T - For Delegate
 */
class NodeEnergyDemandDelegate<T> : PipeBasedCalculableDelegate<T>() {

    override fun recalculateIndexed(index: Int, pipes: List<Pipe>): Double {
        return pipes.sumOf { it.energyDemand[index] }
    }

    override fun onPipeConnect(pipe: Pipe) {
        pipe::energyDemand.subscribeIfChanged(this::updateValue)
    }

}