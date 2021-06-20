package de.fhac.ewi.model

class Optimizer(private val grid: Grid, private val investParams: InvestmentParameter) {

    lateinit var gridCosts: Costs
        private set

    var numberOfTypeChecks: Int = 0
        private set

    var numberOfUpdates: Int = 0
        private set

    fun optimize() {
        // Reset variables
        numberOfTypeChecks = 0
        numberOfUpdates = 0

        // Set all pipes to first possible type
        grid.pipes.forEach { it.type = investParams.pipeTypes.first() }
        gridCosts = investParams.calculateCosts(grid)

        optimizePipesInCriticalPath()

        optimizePipesFromOutputToSource()

        optimizeAllPipes()
    }

    /**
     * Optimize all pipes in
     */
    private fun optimizePipesInCriticalPath() {
        // TODO Find node at the end of critical path (maximum pressure loss in total?)
        // TODO Optimize path from that to source
        val criticalPath = grid.input.criticalChildNode.pathToSource

        for (pipe in criticalPath)
            optimizePipe(pipe)
    }

    /**
     * Starting from all output nodes optimize path to source (one go).
     */
    private fun optimizePipesFromOutputToSource() {
        // TODO Filter all output nodes and calculate path to source for each
        grid.nodes.filterIsInstance<OutputNode>().forEach { node ->
            for (pipe in node.pathToSource)
                optimizePipe(pipe) // TODO Immer nur gleiche/dickere Leitungen probieren?
        }
    }

    /**
     * Check every type on every pipe until no further update is made.
     */
    private fun optimizeAllPipes() {
        var anyPipeUpdated: Boolean
        optimizer@ do {
            anyPipeUpdated = false
            for (pipe in grid.pipes) {
                if (optimizePipe(pipe))
                    anyPipeUpdated = true
            }
        } while (anyPipeUpdated)
    }

    private fun optimizePipe(pipe: Pipe, types: List<PipeType> = investParams.pipeTypes): Boolean {
        var bestType = pipe.type
        var foundBetterType = false
        for (type in types) {
            numberOfTypeChecks++
            pipe.type = type
            val newCost = investParams.calculateCosts(grid)
            if (newCost.totalPerYear < gridCosts.totalPerYear) {
                gridCosts = newCost
                bestType = type
                foundBetterType = true
            }
        }
        if (foundBetterType)
            numberOfUpdates++
        pipe.type = bestType
        return foundBetterType
    }
}