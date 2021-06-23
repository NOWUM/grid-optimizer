package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.volumeFlow


/**
 * ### Volumenstrom in Rohrleitung
 * ... setzt sich aus dem Temperaturunterschied zwischen Vorlauf und Rücklauf und dem Energiebedarf des angeschlossenen Knotenpunkts zusammen.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Die Rohrleitung des Delegates
 * @constructor
 */
class PipeVolumeFlowDelegate<T>(val pipe: Pipe) : LazyCalculableDoubleArray<T>() {

    private val flowIn: DoubleArray by lazy { pipe.source.flowInTemperature.toDoubleArray() } // static
    private val flowOut: DoubleArray by lazy { pipe.source.flowOutTemperature.toDoubleArray() } // static

    init {
        pipe::energyDemand.subscribeIfChanged(this)
    }

    override fun recalculateIndexed(index: Int): Double {
        return volumeFlow(flowIn[index], flowOut[index], pipe.energyDemand[index])
    }

    override fun checkForChanges() {
        pipe.energyDemand // check if energy demand has changed
    }

}