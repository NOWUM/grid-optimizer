package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.neededPumpPower
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.updateIfNeeded

/**
 * ### Benötigte Pumpleistung in Rohrleitung
 * ... setzt sich aus dem Druckverlust und dem Volumenstrom der Rohrleitung zusammen.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Rohrleitung des Delegates
 * @constructor
 */
class PipePumpPowerDelegate<T>(private val pipe: Pipe) : LazyCalculableDoubleArray<T>() {

    init {
        pipe::totalPressureLoss.subscribeIfChanged(this)
        pipe::volumeFlow.subscribeIfChanged(this)
    }

    override fun recalculateIndexed(index: Int) = with(pipe) {
        neededPumpPower(totalPressureLoss[index], volumeFlow[index])
    }

    override fun checkForChanges() {
        pipe::totalPressureLoss.updateIfNeeded() // on change it will trigger recalculation of this property
        pipe::volumeFlow.updateIfNeeded()
    }
}