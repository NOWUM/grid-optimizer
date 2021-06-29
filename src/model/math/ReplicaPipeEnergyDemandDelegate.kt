package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.ReplicaOutputNode
import de.fhac.ewi.model.ReplicaPipe
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.updateIfNeeded

/**
 * ### Energiebedarf in Rohrleitung
 * ... setzt sich aus dem Energiebedarf des angeschlossenen Knotenpunkts (target)
 * und den Wärmeverlusten in der Leitung zusammen.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Rohrleitung des Delegates
 * @constructor
 */
class ReplicaPipeEnergyDemandDelegate<T>(private val pipe: ReplicaPipe) : LazyCalculableDoubleArray<T>() {

    private val replicas: Int = (pipe.target as ReplicaOutputNode).replicas

    init {
        pipe::heatLoss.subscribeIfChanged(this)
        pipe.target::energyDemand.subscribeIfChanged(this)
    }


    override fun recalculate(): DoubleArray {
        val heatLoss = pipe.heatLoss
        val targetEnergyDemand = pipe.target.energyDemand
        return DoubleArray(8760) { index -> (heatLoss[index] + targetEnergyDemand[index]) * replicas }
    }

    override fun checkForChanges() {
        pipe::heatLoss.updateIfNeeded() // on change it will trigger recalculation of this property
        pipe.target::energyDemand.updateIfNeeded()
    }
}