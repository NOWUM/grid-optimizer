package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
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
class PipeEnergyDemandDelegate<T>(private val pipe: Pipe) : LazyCalculableDoubleArray<T>() {

    init {
        pipe::heatLoss.subscribeIfChanged(this)
        pipe.target::energyDemand.subscribeIfChanged(this)
    }

    override fun recalculateIndexed(index: Int) = with(pipe) {
        heatLoss[index] + target.energyDemand[index]
    }

    override fun checkForChanges() {
        pipe::heatLoss.updateIfNeeded() // on change it will trigger recalculation of this property
        pipe.target::energyDemand.updateIfNeeded()
    }
}