package de.fhac.ewi.model.math

import de.fhac.ewi.model.Node
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.updateIfNeeded
import de.fhac.ewi.util.volumeFlow


/**
 * ### Volumenstrom in Knotenpunkt
 * ... wird aus dem Massenstrom bestimmt.
 *
 * @param T - For Delegate
 * @property node Node - Die Knotenpunkt des Delegates
 * @constructor
 */
class NodeVolumeFlowDelegate<T>(val node: Node) : LazyCalculableDoubleArray<T>() {

    init {
        node::massenstrom.subscribeIfChanged(this)
    }

    override fun recalculate(): DoubleArray {
        val massenstrom = node.massenstrom
        return DoubleArray(8760) { index -> volumeFlow(massenstrom[index]) }
    }

    override fun checkForChanges() {
        node::massenstrom.updateIfNeeded() // on change it will trigger recalculation of this property
    }
}