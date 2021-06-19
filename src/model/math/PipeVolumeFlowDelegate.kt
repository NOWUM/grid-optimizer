package de.fhac.ewi.model.math

import de.fhac.ewi.model.Node
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.volumeFlow


/**
 * ### Volumenstrom in Rohrleitung
 * ... setzt sich aus dem Temperaturunterschied zwischen Vorlauf und Rücklauf und dem Energiebedarf des angeschlossenen Knotenpunkts zusammen.
 * TODO Oder aus dem Energiebedarf der Rohrleitung selber?
 *
 * @param T - For Delegate
 * @property flowIn DoubleArray - Vorlauf
 * @property flowOut DoubleArray - Rücklauf
 * @property target Node - Angeschlossener Knotenpunkt
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