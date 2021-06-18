package de.fhac.ewi.model

import de.fhac.ewi.util.DoubleFunction
import kotlin.streams.toList

class InputNode(
    id: String,
    override val groundTemperature: List<Double>,
    flowTemperature: DoubleFunction,
    returnTemperature: DoubleFunction
) : Node(id) {

    override fun canReceiveInputFrom(source: Node): Boolean =
        throw IllegalArgumentException("Input node ${this.id} can not receive input.")

    override val flowInTemperature: List<Double> by lazy { groundTemperature.parallelStream().map(flowTemperature).toList() }

    override val flowOutTemperature: List<Double> by lazy { groundTemperature.parallelStream().map(returnTemperature).toList() }
}