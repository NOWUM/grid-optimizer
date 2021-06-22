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

        // TODO Der maximale Druckverlust verändert sich hier eigentlich nicht mehr.

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
        optimizePipePath(grid.criticalPath.toList())
    }

    /**
     * Starting from all output nodes optimize path to source (one go).
     */
    private fun optimizePipesFromOutputToSource() {
        // Filter all output nodes and calculate path to source for each
        grid.nodes.filterIsInstance<OutputNode>().forEach { node ->
            optimizePipePath(node.pathToSource.toList())
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
    private fun optimizePipe(pipe: Pipe, types: List<PipeType> = investParams.pipeTypes.filterNot { it == pipe.type }): Boolean {
        var bestType = pipe.type
        var foundBetterType = false

        // check all possible types excluding the current pipe type
        types.forEach { type ->
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

    /**
     * Rekursives Verfahren zur Optimierung eines Strangs Richtung Einspeisepunkt.
     *
     * @param pipes Array<Pipe>
     * @param preselectedTypes List<PipeType>
     */
    private fun optimizePipePath(pipes: List<Pipe>, preselectedTypes: List<PipeType> = investParams.pipeTypes): Boolean {
        val currentPipe = pipes.first()

        // Auswahl der möglichen Rohrtypen, die für die Optimierung sinnvoll erscheinen
        val possibleTypes = preselectedTypes.toMutableList()
        currentPipe.target.largestConnectedPipe?.let { largestConnectedPipe ->
            possibleTypes.removeIf { it.diameter < largestConnectedPipe.diameter }
        }

        // Falls es nur eine Pipe im Pfad gibt, dann diese optimieren und ggf updaten.
        if (pipes.size == 1)
            return optimizePipe(currentPipe, possibleTypes)

        // Andernfalls müssen wir den Spaß rekursiv aufrufen.
        var bestType = currentPipe.type
        var betterPathFound = false
        val newPipePath = pipes.drop(1)

        for (type in possibleTypes) {
            currentPipe.type = type

            if (optimizePipePath(newPipePath, possibleTypes)) {
                bestType = currentPipe.type
                betterPathFound = true
            }
        }

        currentPipe.type = bestType
        return betterPathFound
    }
}