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
        optimizePipePath(grid.criticalPath)
    }

    /**
     * Starting from all output nodes optimize path to source (one go).
     */
    private fun optimizePipesFromOutputToSource() {
        // Filter all output nodes and calculate path to source for each
        grid.nodes.filterIsInstance<OutputNode>().forEach { node ->
            optimizePipePath(node.pathToSource)
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

    private fun optimizePipePath(pipes: Array<Pipe>) {
        val bestTypes = pipes.map { it.type }.toMutableList()

        val possibleTypes = investParams.pipeTypes.sortedBy { it.diameter }.toMutableList()
        // If first pipe already has a valid then remove all smaller ones. They can't be right I guess.
        if (pipes.first().type != PipeType.UNDEFINED)
            possibleTypes.removeIf { it.diameter < pipes.first().type.diameter }

        pipes.forEachIndexed { index, pipe ->
            var foundBetterType = false
            // Probiere alle möglichen Types für dieses Rohrstück aus
            for (type in possibleTypes) {
                numberOfTypeChecks++
                // Für die aktuelle Pipe neuen Typ setzen
                pipe.type = type


                // Für alle nachfolgenden Pipes diesen Typ setzen. Es macht keinen Sinn, dass der Rohrdurchmesser kleiner wird.
                for (i in index + 1 until pipes.size)
                    if (bestTypes[i] == PipeType.UNDEFINED || bestTypes[i].diameter < pipes[i-1].type.diameter)
                        pipes[i].type = pipes[i-1].type
                    else
                        pipes[i].type = bestTypes[i]

                // Berechnung der Netzkosten
                val newCost = investParams.calculateCosts(grid)

              /*  println(
                    "Checking ${type.diameter} on ${pipe.id} with parent path ${
                        pipes.drop(index + 1).map { it.id + "(${it.type.diameter})" }
                    } costs are ${newCost.totalPerYear} ${newCost.totalPerYear < gridCosts.totalPerYear} ${grid.neededPumpPower}"
                )*/

                // If costs are lower then old grid or old best type is UNDEFINED -> Update
                if (newCost.totalPerYear < gridCosts.totalPerYear || bestTypes[index] == PipeType.UNDEFINED) {
                    gridCosts = newCost

                    for (i in index until pipes.size)
                        bestTypes[i] = pipes[i].type

                    foundBetterType = true
                    println("Is better!")
                }
            }

            if (foundBetterType)
                numberOfUpdates++
            // Set current pipe to best type found in current check (or before)
            pipe.type = bestTypes[index]
            // Remove all types with a smaller diameter then current pipe. No recheck required
            possibleTypes.removeIf { it.diameter < pipe.type.diameter }
        }

        // Restore best types already done in loop above
    }
}