package de.fhac.ewi.model.math

import de.fhac.ewi.model.Node
import de.fhac.ewi.model.delegate.CalculableDelegate
import de.fhac.ewi.util.neededPumpPower
import de.fhac.ewi.util.subscribeIfChanged

/**
 * ### Benötigte Pumpleistung in einem Knoten
 * berechnet sich aus dem Druckverlust in dem Knoten
 *
 * @param T - For Delegate
 */
class NodePumpPowerDelegate<T>(private val node: Node) : CalculableDelegate<T>() {

    init {
        node::pressureLoss.subscribeIfChanged(this::updateValue)
        // volumeFlow does not need subscription, because it is in totalPressureLoss included
    }

    override fun recalculateIndexed(index: Int) = with(node) {
        neededPumpPower(pressureLoss[index], volumeFlow[index])
    }

}