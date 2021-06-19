package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.volumeFlow


/**
 * ### Volumenstrom in Rohrleitung
 * ... setzt sich aus dem Temperaturunterschied zwischen Vorlauf und Rücklauf und dem Energiebedarf des angeschlossenen Knotenpunkts zusammen.
 * TODO Oder aus dem Energiebedarf der Rohrleitung selber?
 *
 * @param T - For Delegate
 * @property pipe Pipe - Die Rohrleitung des Delegates
 * @constructor
 */
class PipeVolumeFlowDelegate<T>(val pipe: Pipe) : CalculableDelegate<T>() {

    private val flowIn: DoubleArray by lazy { pipe.source.flowInTemperature.toDoubleArray() } // static
    private val flowOut: DoubleArray by lazy { pipe.source.flowOutTemperature.toDoubleArray() } // static

    init {
        pipe.target::energyDemand.subscribeIfChanged(this::updateValue)
    }

    override fun recalculateIndexed(index: Int): Double {
        // TODO Hier direkt Formel für Volumenstrom einsetzen?
        return volumeFlow(flowIn[index], flowOut[index], pipe.target.energyDemand[index])
    }

}