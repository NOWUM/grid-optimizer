package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.updateIfNeeded

/**
 * ### Gesamter Druckverlust in Rohrleitung
 * ... setzt sich aus dem Druckverlust in der Leitung und dem Druckverlust des angeschlossenen Knotenpunkts (target) zusammen.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Rohrleitung des Delegates
 * @constructor
 */
class PipeTotalPressureLossDelegate<T>(private val pipe: Pipe) : LazyCalculableDoubleArray<T>() {

    init {
        pipe::pipePressureLoss.subscribeIfChanged(this)
        pipe.target::pressureLoss.subscribeIfChanged(this)
    }

    override fun recalculate(): DoubleArray {
        val pipePressureLoss = pipe.pipePressureLoss
        val targetPressureLoss = pipe.target.pressureLoss
        return DoubleArray(8760) { index -> pipePressureLoss[index] + targetPressureLoss[index] }
    }

    override fun checkForChanges() {
        pipe::pipePressureLoss.updateIfNeeded() // on change it will trigger recalculation of this property
        pipe.target::pressureLoss.updateIfNeeded()
    }

}