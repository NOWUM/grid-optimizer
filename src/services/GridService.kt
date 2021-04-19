package de.fhac.ewi.services

import de.fhac.ewi.dto.PipeRequest
import de.fhac.ewi.model.*

class GridService {

    internal fun connect(source: Node, target: Node, pipeRequest: PipeRequest): Pipe {
        if (source.isParentOf(target))
            throw IllegalArgumentException("$source is already connected to $target. $pipeRequest invalid.")

        if (target.isParentOf(source))
            throw IllegalArgumentException("$target is already connected to $source. $pipeRequest invalid.")

        val pipe = Pipe(pipeRequest.id, source, target, pipeRequest.length)
        source.connectChild(pipe)
        return pipe
    }

    fun create(nodes: List<Node>, pipeRequests: List<PipeRequest>): Grid {
        if (nodes.count { it is InputNode } != 1)
            throw IllegalArgumentException("Please provide exactly one input node.")

        // connect nodes and create pipes between them
        val pipes = mutableListOf<Pipe>()
        pipeRequests.forEachIndexed { index, pipeRequest ->

            if (pipes.any { it.id == pipeRequest.id })
                throw IllegalArgumentException("ID of $pipeRequest @ index $index already in use!")

            val source = nodes.find { it.id == pipeRequest.source }
                ?: throw IllegalArgumentException("Node for source ${pipeRequest.source} in $pipeRequest @ index $index not found")

            val target = nodes.find { it.id == pipeRequest.target }
                ?: throw IllegalArgumentException("Node for target ${pipeRequest.target} in $pipeRequest @ index $index not found")

            pipes += connect(source, target, pipeRequest)
        }

        // checks per node
        val input = nodes.single { it is InputNode }
        nodes.forEach { node ->
            // check if any node has no connection
            if (node.connectedChildNodes.isEmpty() && node.connectedParentNodes.isEmpty())
                throw IllegalArgumentException("$node has no connection to other nodes.")

            // if node is output it must be connected to input
            if (node is OutputNode && !input.isParentOf(node))
                throw IllegalArgumentException("Output $node has no connection to input node.")
        }

        return Grid(nodes, pipes)
    }
}