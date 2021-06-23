package de.fhac.ewi.model.math

import de.fhac.ewi.model.Node
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.volumeFlow


/**
 * ### Volumenstrom in Knotenpunkt
 * ... setzt sich aus dem Temperaturunterschied zwischen Vorlauf und Rücklauf und dem Energiebedarf des Knotenpunkts und allem was daran angeschlossen ist zusammen.
 *
 * @param T - For Delegate
 * @property node Node - Die Knotenpunkt des Delegates
 * @constructor
 */
class NodeVolumeFlowDelegate<T>(val node: Node) : LazyCalculableDoubleArray<T>() {

    private val flowIn: DoubleArray by lazy { node.flowInTemperature.toDoubleArray() } // static
    private val flowOut: DoubleArray by lazy { node.flowOutTemperature.toDoubleArray() } // static

    init {
        node::energyDemand.subscribeIfChanged(this)
    }

    override fun recalculateIndexed(index: Int): Double {
        return volumeFlow(flowIn[index], flowOut[index], node.energyDemand[index])
    }

    override fun checkForChanges() {
        node.energyDemand
    }
}