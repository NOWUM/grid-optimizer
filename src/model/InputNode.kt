package de.fhac.ewi.model

class InputNode(id: String) : Node(id) {

    override fun canReceiveInputFrom(source: Node): Boolean =
        throw IllegalArgumentException("Input node ($this) can not receive input.")
}