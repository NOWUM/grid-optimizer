package de.fhac.ewi.model

import de.fhac.ewi.exceptions.IllegalGridException
import de.fhac.ewi.util.DoubleFunction
import de.fhac.ewi.util.repeatEach

class Grid {

    private val _nodes = mutableListOf<Node>()
    val nodes: List<Node>
        get() = _nodes.toList()
    private val _pipes = mutableListOf<Pipe>()
    val pipes: List<Pipe>
        get() = _pipes.toList()

    val input: InputNode by lazy { _nodes.filterIsInstance<InputNode>().single() }

    // Returns maximum of needed pump power in W
    val neededPumpPower: Double
        get() = input.pumpPower.maxOrNull()
            ?: throw IllegalStateException("Needed pump power could not retrieved from grid.")

    // Returns energy demand of all outputs in Wh
    val totalOutputEnergy: Double
        get() = nodes.filterIsInstance<OutputNode>().sumOf { it.energyDemand.sum() }

    // Returns total heat loss in all pipes in Wh
    val totalHeatLoss: Double
        get() = pipes.sumOf { it.heatLoss.sum() }

    private fun addNode(node: Node) {
        if (_nodes.any { it.id.equals(node.id, true) })
            throw IllegalArgumentException("There is already an node with id ${node.id}")

        _nodes += node
    }

    fun addInputNode(id: String, groundSeries: TemperatureTimeSeries, flowTemperature: DoubleFunction, returnTemperature: DoubleFunction) {
        if (_nodes.filterIsInstance<InputNode>().count() == 1)
            throw IllegalArgumentException("This grid has already an input node. Only one input node is supported at the moment.")

        addNode(InputNode(id, groundSeries.temperatures.repeatEach(24), flowTemperature, returnTemperature))
    }

    fun addOutputNode(id: String, thermalEnergyDemand: HeatDemandCurve, pressureLoss: Double) {
        addNode(OutputNode(id, thermalEnergyDemand, pressureLoss))
    }

    fun addIntermediateNode(id: String) {
        addNode(IntermediateNode(id))
    }

    fun addPipe(id: String, sourceId: String, targetId: String, length: Double, pipeLayingDepth: Double) {
        if (_pipes.any { it.id.equals(id, true) })
            throw IllegalArgumentException("There is already a pipe with id $id.")

        // Retrieve nodes effected by connection
        val source = _nodes.find { it.id == sourceId }
            ?: throw IllegalArgumentException("Node for source $sourceId not found.")

        val target = _nodes.find { it.id == targetId }
            ?: throw IllegalArgumentException("Node for source $targetId not found.")

        val pipe = Pipe(id, source, target, length, pipeLayingDepth)
        source.connectChild(pipe)
        _pipes += pipe
    }

    fun validate() {
        // All nodes should have a pipe. Otherwise they are useless and should be deleted
        val pipelessNode = _nodes.firstOrNull { it.connectedChildNodes.size + it.connectedParentNodes.size == 0 }
        if (pipelessNode != null)
            throw IllegalGridException("Node ${pipelessNode.id} has no connection to other nodes.")

        // There must be exactly one input
        val inputNode = _nodes.filterIsInstance<InputNode>().singleOrNull()
            ?: throw IllegalGridException("The grid must contain exactly one input node.")

        // inputNode must have a connection to all output nodes
        _nodes.filterIsInstance<OutputNode>().forEach { outputNode ->
            if (!inputNode.isParentOf(outputNode))
                throw IllegalGridException("Output node ${outputNode.id} is not connected to input node ${inputNode.id}.")
        }
    }
}
