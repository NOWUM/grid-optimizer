package de.fhac.ewi.model.math

import de.fhac.ewi.model.Node
import de.fhac.ewi.model.delegate.LazyCalculableDoubleArray
import de.fhac.ewi.util.neededPumpPower
import de.fhac.ewi.util.subscribeIfChanged
import de.fhac.ewi.util.updateIfNeeded

/**
 * ### Benötigte Pumpleistung in einem Knoten
 * berechnet sich aus dem Druckverlust in dem Knoten
 *
 * @param T - For Delegate
 */
class NodePumpPowerDelegate<T>(private val node: Node) : LazyCalculableDoubleArray<T>() {

    init {
        node::pressureLoss.subscribeIfChanged(this)
        node::volumeFlow.subscribeIfChanged(this)
        // volumeFlow does not need subscription, because it is in totalPressureLoss included
    }

    override fun recalculateIndexed(index: Int) = with(node) {
        neededPumpPower(pressureLoss[index], volumeFlow[index])
    }

    override fun checkForChanges() {
        node::pressureLoss.updateIfNeeded() // on change it will trigger recalculation of this property
        node::volumeFlow.updateIfNeeded()
    }
}