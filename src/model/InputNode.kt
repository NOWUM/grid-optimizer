package de.fhac.ewi.model

import de.fhac.ewi.util.DoubleFunction

class InputNode(
    id: String,
    val flowTemperature: DoubleFunction,
    val returnTemperature: DoubleFunction
) : Node(id) {

    override fun canReceiveInputFrom(source: Node): Boolean =
        throw IllegalArgumentException("Input node ($this) can not receive input.")
}