package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.PipeType
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.util.flowRate
import de.fhac.ewi.util.neededPumpPower
import de.fhac.ewi.util.pipePressureLoss
import de.fhac.ewi.util.subscribeIfChanged

/**
 * ### Benötigte Pumpleistung in Rohrleitung
 * ... setzt sich aus dem Druckverlust und dem Volumenstrom der Rohrleitung zusammen.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Rohrleitung des Delegates
 * @constructor
 */
class PipePumpPowerDelegate<T>(private val pipe: Pipe) :
    CalculableDelegate<T>() {

    init {
        pipe::totalPressureLoss.subscribeIfChanged(this::updateValue)
        // volumeFlow does not need subscription, because it is in totalPressureLoss included
    }

    override fun recalculateIndexed(index: Int) = with(pipe) {
        neededPumpPower(totalPressureLoss[index], volumeFlow[index])
    }

}