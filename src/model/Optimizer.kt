package de.fhac.ewi.model

import de.fhac.ewi.util.round

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


        optimizeCriticalPipesOneByOne()
        println(
            "Critical one by one $numberOfTypeChecks checks and $numberOfUpdates updates (current costs are ${
                gridCosts.totalPerYear.round(
                    2
                )
            })"
        )

        optimizeCriticalPipePath()
        println(
            "Critical hole path $numberOfTypeChecks checks and $numberOfUpdates updates (current costs are ${
                gridCosts.totalPerYear.round(
                    2
                )
            })"
        )

        // TODO Der maximale Druckverlust verändert sich hier eigentlich nicht mehr.
        println("Max pressure loss after critical path: " + grid.input.pressureLoss.maxOrNull())

        optimizeUnsetPathsFromOutputToSourceUntilNoChange()
        println(
            "Out2Source $numberOfTypeChecks checks and $numberOfUpdates updates (current costs are ${
                gridCosts.totalPerYear.round(
                    2
                )
            })"
        )


        optimizeAllPipes()
        println(
            "All $numberOfTypeChecks checks and $numberOfUpdates updates (current costs are ${
                gridCosts.totalPerYear.round(
                    2
                )
            })"
        )

        println("Max pressure loss at the end of optimization: " + grid.input.pressureLoss.maxOrNull())
    }

    private fun optimizeCriticalPipesOneByOne() {
        for (pipe in grid.criticalPath) {
            optimizePipe(pipe, fastMode = true)
        }
    }

    /**
     * Optimize all pipes in critical path
     */
    private fun optimizeCriticalPipePath() {
        // CriticalPath = Longest Path from OutputNode to InputNode
        optimizePipePath(grid.criticalPath.toList())
    }

    // Nicht ganz so geil
    private fun optimizeUnsetPathsFromOutputToSourceOneByOne() {
        grid.nodes.filterIsInstance<OutputNode>().forEach { node ->
            val path = node.pathToSource
            if (path.first().type == PipeType.UNDEFINED)
                for (pipe in path)
                    optimizePipe(pipe, fastMode = true)
        }
    }

    /**
     * Starting from all output nodes optimize path to source (one go).
     */
    private fun optimizeUnsetPathsFromOutputToSource() {
        // Filter all output nodes and calculate path to source for each
        grid.nodes.filterIsInstance<OutputNode>().forEach { node ->
            val path = node.pathToSource
            if (path.first().type == PipeType.UNDEFINED)
                optimizePipePath(path.toList(), fastMode = true)
        }
    }

    private fun optimizeUnsetPathsFromOutputToSourceUntilNoChange() {
        var anyPathUpdated: Boolean
        do {
            anyPathUpdated = false
            grid.nodes.filterIsInstance<OutputNode>().forEach { node ->
                if (optimizePipePath(node.pathToSource.toList(), fastMode = false))
                    anyPathUpdated = true
            }
        } while (anyPathUpdated)
    }

    /**
     * Check every type on every pipe until no further update is made.
     */
    private fun optimizeAllPipes() {
        var anyPipeUpdated: Boolean
        do {
            anyPipeUpdated = false
            for (pipe in grid.pipes) {
                if (optimizePipe(pipe, fastMode = true))
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
    private fun optimizePipe(
        pipe: Pipe,
        types: List<PipeType> = investParams.pipeTypes.filterNot { it == pipe.type },
        fastMode: Boolean = false
    ): Boolean {
        var bestType = pipe.type
        var foundBetterType = false

        val skipIfTypesAreGettingWorse = (bestType != PipeType.UNDEFINED && fastMode)
        val biggestSubtype = if (bestType != PipeType.UNDEFINED && fastMode) pipe.target.largestConnectedPipe else null

        // check all possible types excluding the current pipe type
        for (type in types) {

            if (biggestSubtype != null && type.diameter < biggestSubtype.diameter)
                continue

            numberOfTypeChecks++
            pipe.type = type
            val newCost = investParams.calculateCosts(grid)
            if (newCost.totalPerYear < gridCosts.totalPerYear || bestType == PipeType.UNDEFINED) {
                gridCosts = newCost
                bestType = type
                foundBetterType = true
            } else if (foundBetterType && skipIfTypesAreGettingWorse)
                break
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
    private fun optimizePipePath(
        pipes: List<Pipe>,
        preselectedTypes: List<PipeType> = investParams.pipeTypes,
        fastMode: Boolean = true
    ): Boolean {
        val currentPipe = pipes.first()

        // Weitere Eingrenzung der möglichen Rohrtypen, die für die Optimierung sinnvoll erscheinen
        val possibleTypes = preselectedTypes.toMutableList()
        // Current pipe type musst be larger then largest pipe connected
        currentPipe.target.largestConnectedPipe?.let { largestConnectedPipe ->
            possibleTypes.removeIf { it.diameter < largestConnectedPipe.diameter }
        }
        // If pipe type already set only check bigger or equal types then currently set
        if (currentPipe.type != PipeType.UNDEFINED && fastMode)
            possibleTypes.removeIf { it.diameter < currentPipe.type.diameter }

        // Falls es nur eine Pipe im Pfad gibt, dann diese optimieren und ggf updaten.
        if (pipes.size == 1)
            return optimizePipe(currentPipe, possibleTypes)

        // Andernfalls müssen wir den Spaß rekursiv aufrufen.
        var bestType = currentPipe.type
        var betterPathFound = false
        val newPipePath = pipes.drop(1)

        val skipIfTypesAreGettingWorse = (bestType != PipeType.UNDEFINED && fastMode)

        for (type in possibleTypes) {
            currentPipe.type = type

            if (optimizePipePath(newPipePath, possibleTypes, fastMode)) {
                bestType = currentPipe.type
                betterPathFound = true
            } else if (betterPathFound && skipIfTypesAreGettingWorse)
                break
        }

        currentPipe.type = bestType
        return betterPathFound
    }

    // TODO Bringt ebenfalls nichts
    private fun optimizeAllConnectedPipes(node: Node = grid.input) {
        for (child in node.connectedChildNodes)
            if (child.connectedChildPipes.isNotEmpty())
                optimizeAllConnectedPipes(child)

        optimizeChildPipesOfNode(node.connectedChildPipes)
    }

    private fun optimizeChildPipesOfNode(pipes: List<Pipe>): Boolean {
        if (pipes.size == 1)
            return optimizePipe(pipes.first(), fastMode = true)

        val currentPipe = pipes.first()
        var bestType = currentPipe.type
        var betterChildCombinationFound = false
        val remainingChildPipes = pipes.drop(1)

        val possibleTypes =
            investParams.pipeTypes.filter { currentPipe.type == PipeType.UNDEFINED || it.diameter > currentPipe.type.diameter }

        for (type in possibleTypes) {
            currentPipe.type = type

            if (optimizeChildPipesOfNode(remainingChildPipes)) {
                bestType = currentPipe.type
                betterChildCombinationFound = true
            }
        }

        currentPipe.type = bestType
        return betterChildCombinationFound
    }
}