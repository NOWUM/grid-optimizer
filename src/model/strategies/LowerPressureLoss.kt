package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.InputNode
import de.fhac.ewi.model.Node
import de.fhac.ewi.model.Optimizer

/**
 * Diese Strategie versucht den maximalen Druckverlust im Netz zu senken, indem die "schlimmsten" Leitungen zeitgleich optimiert werden
 */
object LowerPressureLoss : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        checkNode(grid.input)
    }

    private fun Optimizer.checkNode(node: Node) {
        node.connectedChildNodes.forEach { child ->
            if (child.connectedChildPipes.size > 1)
                checkNode(child)
        }

        optimizePipes(node.connectedChildPipes + node.pathToSource, skipSmallerThenCurrent=true, maxTries = 3)
    }
}