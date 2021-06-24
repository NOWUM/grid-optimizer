package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Node
import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.PipeType

/**
 * Optimiert zu jedem Knotenpunkt alle daran angeschlossenen Leitungen auf einer Ebene.
 * Dabei werden aber nur die Rohrleitungen probiert, die von den anderen Childs genutzt werden.
 */
object ChildNodesOfPipe : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        optimizeAllConnectedPipes(optimizer, grid.input)
    }

    private fun optimizeAllConnectedPipes(optimizer: Optimizer, node: Node) {
        for (child in node.connectedChildNodes)
            if (child.connectedChildPipes.isNotEmpty())
                optimizeAllConnectedPipes(optimizer, child)

        if (node.connectedChildPipes.size > 1)
            optimizeChildPipesOfNode(
                optimizer,
                node.connectedChildPipes,
                node.connectedChildPipes.map { it.type }.distinct()
            )
    }

    private fun optimizeChildPipesOfNode(
        optimizer: Optimizer,
        pipes: List<Pipe>,
        possibleTypes: List<PipeType>
    ): Boolean = with(optimizer) {
        if (pipes.size == 1)
            return optimizePipe(pipes.first(), possibleTypes, fastMode = true)

        val currentPipe = pipes.first()
        var bestType = currentPipe.type
        var betterChildCombinationFound = false
        val remainingChildPipes = pipes.drop(1)

        for (type in possibleTypes) {
            currentPipe.type = type

            if (optimizeChildPipesOfNode(optimizer, remainingChildPipes, possibleTypes)) {
                bestType = currentPipe.type
                betterChildCombinationFound = true
            }
        }

        currentPipe.type = bestType
        return betterChildCombinationFound
    }
}