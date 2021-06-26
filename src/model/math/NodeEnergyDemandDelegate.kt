package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.PipeBasedCalculableDelegate
import de.fhac.ewi.util.updateIfNeeded
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

    override fun recalculate(pipes: List<Pipe>): DoubleArray {
        val energyDemands = pipes.map { it.energyDemand }
        return DoubleArray(8760) {index -> energyDemands.sumOf { it[index] } }
    }

    override fun onPipeConnect(pipe: Pipe) {
        pipe::energyDemand.subscribeIfChanged(this)
    }

    override fun onPossiblePipeUpdate(pipe: Pipe) {
        pipe::energyDemand.updateIfNeeded() // on change it will trigger recalculation of this property
    }

}