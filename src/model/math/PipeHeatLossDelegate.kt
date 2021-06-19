package de.fhac.ewi.model.math

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.PipeType
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.util.pipeHeatLoss
import de.fhac.ewi.util.subscribeIfChanged

/**
 * ### Wärmeverlust in Rohrleitung
 * ... setzt sich aus der Temperaturdifferenz und der Beschaffenheit der Rohrleitung zusammen.
 *
 * @param T - For Delegate
 * @property flowIn DoubleArray - Vorlauf
 * @property flowOut DoubleArray - Rücklauf
 * @property pipe Pipe - Rohrleitung des Delegates
 * @constructor
 */
class PipeHeatLossDelegate<T>(
    private val flowIn: DoubleArray, // static
    private val flowOut: DoubleArray,  // static
    private val pipe: Pipe
) : CalculableDelegate<T>() {

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