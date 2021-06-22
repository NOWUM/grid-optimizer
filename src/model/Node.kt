package de.fhac.ewi.model

import de.fhac.ewi.model.math.NodeEnergyDemandDelegate
import de.fhac.ewi.model.math.NodePressureLossDelegate
import de.fhac.ewi.model.math.NodePumpPowerDelegate
import de.fhac.ewi.model.math.NodeVolumeFlowDelegate
import de.fhac.ewi.util.addPipeIfNeeded

abstract class Node(val id: String) {

    init {
        if (id.isBlank())
            throw IllegalArgumentException("Id of node must be filled.")
    }

    private val connectedPipes = mutableListOf<Pipe>()

    val connectedChildNodes: List<Node>
        get() = connectedPipes.filter { it.source == this }.map { it.target }

    val connectedParentNodes: List<Node>
        get() = connectedPipes.filter { it.target == this }.map { it.source }


    // complex attributes
    open val energyDemand by NodeEnergyDemandDelegate()

    open val volumeFlow by NodeVolumeFlowDelegate(this)

    open val pressureLoss by NodePressureLossDelegate()

    open val pumpPower by NodePumpPowerDelegate(this)


    open val flowInTemperature: List<Double>
        get() = connectedPipes.single { it.target == this }.source.flowInTemperature

    open val flowOutTemperature: List<Double>
        get() = connectedPipes.single { it.target == this }.source.flowOutTemperature

    open val groundTemperature: List<Double>
        get() = connectedPipes.single { it.target == this }.source.groundTemperature

    open val pathToSource: Array<Pipe> by lazy {
        connectedPipes.single { it.target == this }.let { arrayOf(it, *it.source.pathToSource) }
    }

    open val largestConnectedPipe: PipeType?
        get() = connectedPipes.filter { it.source == this && it.type != PipeType.UNDEFINED }.maxByOrNull { it.type.diameter }?.type

    fun isParentOf(target: Node): Boolean =
        target in connectedChildNodes || connectedChildNodes.any { it.isParentOf(target) }

    open fun canReceiveInputFrom(source: Node) = !isParentOf(source) && connectedParentNodes.isEmpty()

    open fun connectChild(pipe: Pipe) {
        if (pipe.source != this)
            throw IllegalArgumentException("Source of ${pipe.id} does not match ${this.id}.")

        if (isParentOf(pipe.target))
            throw IllegalArgumentException("${this.id} is already parent of ${pipe.target.id}")

        if (!pipe.target.canReceiveInputFrom(this))
            throw IllegalArgumentException("${pipe.target.id} can not receive input from ${this.id}.")

        connectedPipes += pipe
        pipe.target.connectedPipes += pipe

        // Include child pipe in property calculation
        this::energyDemand.addPipeIfNeeded(pipe)
        this::pressureLoss.addPipeIfNeeded(pipe)
    }

}