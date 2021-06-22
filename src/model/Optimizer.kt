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

        // Reset all pipes to undefined type
        grid.pipes.forEach { it.type = PipeType.UNDEFINED }
        gridCosts = investParams.calculateCosts(grid)

        optimizePipesInCriticalPath()
        println("Critical $numberOfTypeChecks checks and $numberOfUpdates updates")

        optimizePipesFromOutputToSource()
        println("Out2Source $numberOfTypeChecks checks and $numberOfUpdates updates")

        optimizeAllPipes()
        println("All $numberOfTypeChecks checks and $numberOfUpdates updates")
    }

    /**
     * Optimize all pipes in critical path
     */
    private fun optimizePipesInCriticalPath() {
        // CriticalPath = Longest Path from OutputNode to InputNode
        for (pipe in grid.criticalPath)
            optimizePipe(pipe)
    }

    /**
     * Starting from all output nodes optimize path to source (one go).
     */
    private fun optimizePipesFromOutputToSource() {
        // Filter all output nodes and calculate path to source for each
        grid.nodes.filterIsInstance<OutputNode>().forEach { node ->
            val possiblePipes = investParams.pipeTypes.toMutableList()
            for (pipe in node.pathToSource) {
                optimizePipe(pipe, possiblePipes)
                possiblePipes.removeIf { pipe.type.diameter > it.diameter }
            }
        }
    }

    /**
     * Check every type on every pipe until no further update is made.
     */
    private fun optimizeAllPipes() {
        var anyPipeUpdated: Boolean
        do {
            anyPipeUpdated = false
            for (pipe in grid.pipes) {
                if (optimizePipe(pipe))
                    anyPipeUpdated = true
            }
        } while (anyPipeUpdated)
    }

    /**
     * Checks all possible types in pipe and check if total costs are lower.
     *
     * @param pipe Pipe - Rohrleitung für die die Gesamtkosten minimiert werden sollen
     * @param types List<PipeType> - Liste mit möglichen Rohrtypen
     * @return Boolean - true, wenn Optimierung vorgenommen wurde. Sonst false
     */
    private fun optimizePipe(pipe: Pipe, types: List<PipeType> = investParams.pipeTypes): Boolean {
        var bestType = pipe.type
        var foundBetterType = false

        // check all possible types excluding the current pipe type
        types.filterNot { it == pipe.type }.forEach { type ->
            numberOfTypeChecks++
            pipe.type = type
            val newCost = investParams.calculateCosts(grid)
            if (newCost.totalPerYear < gridCosts.totalPerYear || bestType == PipeType.UNDEFINED) {
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