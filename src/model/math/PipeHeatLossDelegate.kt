package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.CalculableDelegate
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
class PipeHeatLossDelegate<T>(private val pipe: Pipe) : CalculableDelegate<T>() {

    private val flowIn: DoubleArray by lazy { pipe.source.flowInTemperature.toDoubleArray() } // static
    private val flowOut: DoubleArray by lazy { pipe.source.flowOutTemperature.toDoubleArray() } // static

    init {
        pipe::type.subscribeIfChanged(this::updateValue)
    }

    override fun recalculateIndexed(index: Int) = with(pipe) {
        // TODO Hier direkt Formel für pipeHeatLoss einsetzen?
        pipeHeatLoss(
            flowIn[index],
            flowOut[index],
            10.0, // TODO static
            type.diameter,
            type.isolationThickness,
            coverageHeight,
            type.distanceBetweenPipes,
            length
        )
    }

}