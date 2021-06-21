package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.util.flowRate
import de.fhac.ewi.util.subscribeIfChanged

/**
 * ### Strömungsgeschwindigkeit in Rohrleitung
 * ... setzt sich aus dem Volumenstrom und dem Durchmesser der Rohrleitung zusammen.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Rohrleitung des Delegates
 * @constructor
 */
class PipeFlowRateDelegate<T>(private val pipe: Pipe) : CalculableDelegate<T>() {

    init {
        pipe::volumeFlow.subscribeIfChanged(this::updateValue)
        pipe::type.subscribeIfChanged(this::updateValue)
    }

    override fun recalculateIndexed(index: Int) = with(pipe) {
        // TODO ggf anders berechnen, da der diameter jedes mal zur gleichen Fläche übertragen wird
        flowRate(type.diameter, volumeFlow[index])
    }

}