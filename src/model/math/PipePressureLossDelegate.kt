package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.pipePressureLoss
import de.fhac.ewi.util.subscribeIfChanged

/**
 * ### Druckverlust in Rohrleitung
 * ... setzt sich aus der Strömungsgeschwindigkeit, dem Durchmesser und Länge der Rohrleitung zusammen.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Rohrleitung des Delegates
 * @constructor
 */
class PipePressureLossDelegate<T>(private val pipe: Pipe) : LazyCalculableDoubleArray<T>() {

    init {
        pipe::flowRate.subscribeIfChanged(this)
        pipe::type.subscribeIfChanged(this)
    }

    override fun recalculateIndexed(index: Int) = with(pipe) {
        pipePressureLoss(flowRate[index], length, type.diameter) * 2
    }

    override fun checkForChanges() {
        pipe.flowRate // check if flowRate has changed
        // type changes will directly result in recalculation
    }

}