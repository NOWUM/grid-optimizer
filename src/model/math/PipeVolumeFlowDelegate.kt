package de.fhac.ewi.model.math

import de.fhac.ewi.model.Node
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.volumeFlow

/**
 * TODO
 *
 * TODO Wird der Volumenstrom mit dem Ausgangsmassenstrom oder dem eingangsmassenstrom berechnet?
 * @param T
 * @property flowIn DoubleArray
 * @property flowOut DoubleArray
 * @property target Node
 * @constructor
 */
class PipeVolumeFlowDelegate<T>(val flowIn: DoubleArray, val flowOut: DoubleArray, val target: Node) :
    CalculableDelegate<T>() {

    init {
        target::energyDemand.subscribeIfChanged(this::updateValue)
    }

    override fun recalculateIndexed(index: Int): Double {
        // TODO Hier direkt Formel für Volumenstrom einsetzen?
        return volumeFlow(flowIn[index], flowOut[index], target.energyDemand[index])
    }

}