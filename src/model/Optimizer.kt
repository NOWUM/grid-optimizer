package de.fhac.ewi.model

import de.fhac.ewi.model.strategies.*
import de.fhac.ewi.util.round

class Optimizer(val grid: Grid, val investParams: InvestmentParameter) {

    lateinit var gridCosts: Costs

    var completed: Boolean = false
        private set

    var numberOfTypeChecks: Int = 0
        private set

    var numberOfUpdates: Int = 0
        private set

    fun optimize() {
        completed = false
        // Reset variables
        numberOfTypeChecks = 0
        numberOfUpdates = 0

        val initial = PipeType.UNDEFINED//investParams.pipeTypes.last() //
        // Reset all pipes to undefined type
        grid.pipes.forEach { it.type = initial }
        gridCosts = investParams.calculateCosts(grid)

        val strategies: List<Strategy> = listOf(RepeatAllOneByOne)

        strategies.forEach { strategy ->
            val oldNumberOfTypeChecks = numberOfTypeChecks
            val oldNumberOfUpdates = numberOfUpdates
            strategy.apply(this)
            println("> Applied strategy ${strategy.javaClass.simpleName}. Grid costs now ${gridCosts.totalPerYear.round(2)} €\n" +
                    ">> Number of type checks: ${numberOfTypeChecks - oldNumberOfTypeChecks} times ($numberOfTypeChecks total)\n" +
                    ">> Number of type update: ${numberOfUpdates - oldNumberOfUpdates} times ($numberOfUpdates total)\n" +
                    ">> Maximum pressure loss: ${grid.input.pressureLoss.maxOrNull()} Bar (${(grid.input.pressureLoss.maxOrNull()?:0.0) * 100_000 / grid.criticalPath.sumOf { it.length }} Pa/m)")
        }
        completed = true
    }

    /**
     * Checks all possible types in pipe and check if total costs are lower.
     *
     * @param pipe Pipe - Rohrleitung für die die Gesamtkosten minimiert werden sollen
     * @param types List<PipeType> - Liste mit möglichen Rohrtypen
     * @return Boolean - true, wenn Optimierung vorgenommen wurde. Sonst false
     */
    internal fun optimizePipe(
        pipe: Pipe,
        types: List<PipeType> = investParams.pipeTypes.filterNot { it == pipe.type },
        fastMode: Boolean = false
    ): Boolean {
        var bestType = pipe.type
        //var currentMaxPressureLoss = grid.input.pressureLoss.maxOrNull()?:0.0
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
            // if ((newCost.totalPerYear < gridCosts.totalPerYear || bestType == PipeType.UNDEFINED) && grid.input.pressureLoss.maxOrNull()?:0.0 <= currentMaxPressureLoss * 1.01) {
            if (newCost.totalPerYear < gridCosts.totalPerYear || bestType == PipeType.UNDEFINED) {
                gridCosts = newCost
                bestType = type
                //currentMaxPressureLoss = grid.input.pressureLoss.maxOrNull()?:0.0
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
    internal fun optimizePipePath(
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
        //if (currentPipe.type != PipeType.UNDEFINED && fastMode)
          //  possibleTypes.removeIf { it.diameter < currentPipe.type.diameter }

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


}