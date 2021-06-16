package de.fhac.ewi.model

import de.fhac.ewi.util.DoubleFunction

class InputNode(
    id: String,
    override val groundTemperature: List<Double>,
    flowTemperature: DoubleFunction,
    returnTemperature: DoubleFunction
) : Node(id) {

    override fun canReceiveInputFrom(source: Node): Boolean =
        throw IllegalArgumentException("Input node ($this) can not receive input.")

    override val flowInTemperature: List<Double> by lazy { groundTemperature.map(flowTemperature) }

    override val flowOutTemperature: List<Double> by lazy { groundTemperature.map(returnTemperature) }
}