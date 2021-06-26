package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.*

object IslandStrategy : Strategy {
    override fun apply(optimizer: Optimizer) = with(optimizer) {
        optimizeSubgrid(grid.input)
        gridCosts = investParams.calculateCosts(grid)
    }

    private fun Optimizer.optimizeSubgrid(node: Node) {
        val intermediates = node.connectedChildNodes.filterIsInstance<IntermediateNode>()

        // Rekursiven Aufruf zur Optimierung des Subgrids für sich "allein"
        for (intermediate in intermediates)
            optimizeSubgrid(intermediate)

        var costs = investParams.calculateCosts(node)

        // Nochmal die Pfade nach optimieren unter Berücksichtigung der Gesamtkosten resultierend aus den anderen Strängen
        for (intermediate in intermediates)
            costs = optimizeSubgridForMaxCosts(intermediate, costs) { investParams.calculateCosts(node) }

        // Alle direkt angeschlossenen Rohrleitungen auf einmal optimieren, um das beste Gesamtergebnis zu erzielen
        costs = optimizeMultiplePipes(node.connectedChildPipes, costs) { investParams.calculateCosts(node) }

        // TODO Ggf in Subgrids Rohrdurchmesser nochmal anpassen, falls dadurch die Gesamtkosten sinken
    }

    private fun Optimizer.optimizeSubgridForMaxCosts(node: IntermediateNode, currentlyBest: Costs, calculateCosts: () -> Costs): Costs {
        var bestCosts = currentlyBest

        val intermediates = node.connectedChildNodes.filterIsInstance<IntermediateNode>()

        // Rekursiven Aufruf zur Optimierung des Subgrids für sich "allein"
        for (intermediate in intermediates)
            bestCosts = optimizeSubgridForMaxCosts(intermediate, bestCosts, calculateCosts)

        // prüfen ob einzelne Pipes verbessert werden könnten
        bestCosts = optimizeMultiplePipes(node.connectedChildPipes, currentlyBest, calculateCosts)

        return bestCosts
    }

    private inline fun Optimizer.optimizeMultiplePipes(pipes: List<Pipe>, currentlyBest: Costs, calculateCosts: () -> Costs): Costs {
        var bestCosts = currentlyBest

        var anyChanges: Boolean
        do {
            anyChanges = false
            for (pipe in pipes.sortedByDescending { it.length }) {
                val newCosts = optimizePipeCosts(pipe, investParams.pipeTypes, bestCosts, calculateCosts)
                if (newCosts.totalPerYear < bestCosts.totalPerYear) {
                    bestCosts = newCosts
                    anyChanges = true
                }
            }
        } while (anyChanges)

        return bestCosts
    }

    private inline fun optimizePipeCosts(
        pipe: Pipe,
        types: List<PipeType>,
        bestCosts: Costs,
        calculateCosts: () -> Costs
    ): Costs {
        var costs = bestCosts
        var bestType = pipe.type
        var foundBetterType = false

        val skipIfTypesAreGettingWorse = (bestType != PipeType.UNDEFINED)
        val biggestSubtype = if (bestType != PipeType.UNDEFINED) pipe.target.largestConnectedPipe else null

        // check all possible types excluding the current pipe type
        for (type in types) {

            if (biggestSubtype != null && type.diameter < biggestSubtype.diameter)
                continue

            pipe.type = type
            val newCost = calculateCosts()
            if (newCost.totalPerYear < costs.totalPerYear) {
                costs = newCost
                bestType = type
                foundBetterType = true
            } else if (foundBetterType && skipIfTypesAreGettingWorse)
                break
        }

        pipe.type = bestType
        return costs
    }
}