package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.util.subscribeIfChanged

/**
 * ### Energiebedarf in Rohrleitung
 * ... setzt sich aus dem Energiebedarf des angeschlossenen Knotenpunkts (target)
 * und den Wärmeverlusten in der Leitung zusammen.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Rohrleitung des Delegates
 * @constructor
 */
class PipeEnergyDemandDelegate<T>(private val pipe: Pipe) : CalculableDelegate<T>() {

    init {
        pipe::heatLoss.subscribeIfChanged(this::updateValue)
        pipe.target::energyDemand.subscribeIfChanged(this::updateValue)
    }

    override fun recalculateIndexed(index: Int) = with(pipe) {
        heatLoss[index] + target.energyDemand[index]
    }

}