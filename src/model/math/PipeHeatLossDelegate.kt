package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.pipeHeatLoss
import de.fhac.ewi.util.subscribeIfChanged

/**
 * ### Wärmeverlust in Rohrleitung
 * ... setzt sich aus der Temperaturdifferenz und der Beschaffenheit der Rohrleitung zusammen.
 *
 * @param T - For Delegate
 * @property pipe Pipe - Rohrleitung des Delegates
 * @constructor
 */
class PipeHeatLossDelegate<T>(private val pipe: Pipe) : LazyCalculableDoubleArray<T>() {

    private val flowIn: DoubleArray by lazy { pipe.source.flowInTemperature.toDoubleArray() } // static
    private val flowOut: DoubleArray by lazy { pipe.source.flowOutTemperature.toDoubleArray() } // static

    init {
        pipe::type.subscribeIfChanged(this)
    }

    override fun recalculateIndexed(index: Int) = with(pipe) {
        pipeHeatLoss(
            flowIn[index],
            flowOut[index],
            10.0,
            type.diameter,
            type.isolationThickness,
            coverageHeight,
            type.distanceBetweenPipes,
            length
        )
    }


    override fun checkForChanges() {
        // type changes will directly result in recalculation
    }

}