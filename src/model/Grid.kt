package de.fhac.ewi.model

import de.fhac.ewi.exceptions.IllegalGridException

class Grid {

    private val nodes = mutableListOf<Node>()
    private val pipes = mutableListOf<Pipe>()

    private fun addNode(node: Node) {
        if (nodes.any { it.id.equals(node.id, true) })
            throw IllegalArgumentException("There is already an node with id ${node.id}")

        nodes += node
    }

    fun addInputNode(id: String) {
        if (nodes.filterIsInstance<InputNode>().count() == 1)
            throw IllegalArgumentException("This grid has already an input node. Only one input node is supported at the moment.")

        addNode(InputNode(id))
    }

    fun addOutputNode(id: String, thermalEnergyDemand: Double, pressureLoss: Double) {
        addNode(OutputNode(id, thermalEnergyDemand, pressureLoss))
    }

    fun addIntermediateNode(id: String) {
        addNode(IntermediateNode(id))
    }

    fun addPipe(id: String, sourceId: String, targetId: String, length: Double) {
        if (pipes.any { it.id.equals(id, true) })
            throw IllegalArgumentException("There is already a pipe with id $id")

        // Retrieve nodes effected by connection
        val source = nodes.find { it.id == sourceId }
            ?: throw IllegalArgumentException("Node for source $sourceId not found.")

        val target = nodes.find { it.id == targetId }
            ?: throw IllegalArgumentException("Node for source $targetId not found.")

        val pipe = Pipe(id, source, target, length)
        source.connectChild(pipe)
        pipes += pipe
    }

    fun validate() {
        // All nodes should have a pipe. Otherwise they are useless and should be deleted
        val pipelessNode = nodes.firstOrNull { it.connectedChildNodes.size + it.connectedParentNodes.size == 0 }
        if (pipelessNode != null)
            throw IllegalGridException("Node $pipelessNode has no connection to other nodes.")

        // There must be exactly one input
        val inputNode = nodes.filterIsInstance<InputNode>().singleOrNull()
            ?: throw IllegalGridException("The grid must contain exactly one input node.")

        // inputNode must have a connection to all output nodes
        nodes.filterIsInstance<OutputNode>().forEach { outputNode ->
            if (!inputNode.isParentOf(outputNode))
                throw IllegalGridException("$outputNode is not connected to input node $inputNode")
        }
    }
}
