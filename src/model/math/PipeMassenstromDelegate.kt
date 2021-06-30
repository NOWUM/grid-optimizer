package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.massenstrom
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.updateIfNeeded
import de.fhac.ewi.util.volumeFlow


/**
 * ### Massenstrom in Rohrleitung
 * ... setzt sich aus dem Temperaturunterschied zwischen Vorlauf und Rücklauf und dem Energiebedarf des angeschlossenen Knotenpunkts zusammen.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Die Rohrleitung des Delegates
 * @constructor
 */
class PipeMassenstromDelegate<T>(val pipe: Pipe) : LazyCalculableDoubleArray<T>() {

    private val flowIn: DoubleArray by lazy { pipe.source.flowInTemperature.toDoubleArray() } // static
    private val flowOut: DoubleArray by lazy { pipe.source.flowOutTemperature.toDoubleArray() } // static

    init {
        pipe::heatLoss.subscribeIfChanged(this)
        pipe.target::energyDemand.subscribeIfChanged(this)
    }

    override fun recalculate(): DoubleArray {
        val heatLoss = pipe.heatLoss
        val targetEnergyDemand = pipe.target.energyDemand
        return DoubleArray(8760) { index -> massenstrom(flowIn[index], flowOut[index], heatLoss[index] + targetEnergyDemand[index]) }
    }

    override fun checkForChanges() {
        pipe::heatLoss.updateIfNeeded() // on change it will trigger recalculation of this property
        pipe.target::energyDemand.updateIfNeeded() // on change it will trigger recalculation of this property
    }

}