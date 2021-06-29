package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.flowRate
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.updateIfNeeded

/**
 * ### Strömungsgeschwindigkeit in Rohrleitung
 * ... setzt sich aus dem Volumenstrom und dem Durchmesser der Rohrleitung zusammen.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Rohrleitung des Delegates
 * @constructor
 */
class PipeFlowRateDelegate<T>(private val pipe: Pipe) : LazyCalculableDoubleArray<T>() {

    init {
        pipe::volumeFlow.subscribeIfChanged(this)
        pipe::type.subscribeIfChanged(this)
    }


    override fun recalculate(): DoubleArray {
        val volumeFlow = pipe.volumeFlow
        // TODO ggf querschnitt schon vorberechnen?
        return DoubleArray(8760) { index -> flowRate(pipe.type.diameter, volumeFlow[index]) }
    }

    override fun checkForChanges() {
        pipe::volumeFlow.updateIfNeeded() // on change it will trigger recalculation of this property
        // type changes will directly result in recalculation
    }
}