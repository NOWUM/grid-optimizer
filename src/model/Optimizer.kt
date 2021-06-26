package de.fhac.ewi.model

import de.fhac.ewi.model.strategies.AllCombinations
import de.fhac.ewi.model.strategies.Strategy
import de.fhac.ewi.util.round

class Optimizer(val grid: Grid, val investParams: InvestmentParameter) {

    lateinit var gridCosts: Costs


    var numberOfTypeChecks: Int = 0
        private set

    var numberOfUpdates: Int = 0
        private set

    fun optimize() {
        // Reset variables
        numberOfTypeChecks = 0
        numberOfUpdates = 0

        val initial = PipeType.UNDEFINED//investParams.pipeTypes.last() //
        // Reset all pipes to undefined type
        grid.pipes.forEach { it.type = initial }
        gridCosts = investParams.calculateCosts(grid)

        val strategies: List<Strategy> = listOf(AllCombinations)

        strategies.forEach { strategy ->
            val oldNumberOfTypeChecks = numberOfTypeChecks
            val oldNumberOfUpdates = numberOfUpdates
            strategy.apply(this)
            println("> Applied strategy ${strategy.javaClass.simpleName}. Grid costs now ${gridCosts.totalPerYear.round(2)} €\n" +
                    ">> Number of type checks: ${numberOfTypeChecks - oldNumberOfTypeChecks} times ($numberOfTypeChecks total)\n" +
                    ">> Number of type update: ${numberOfUpdates - oldNumberOfUpdates} times ($numberOfUpdates total)\n" +
                    ">> Maximum pressure loss: ${grid.input.pressureLoss.maxOrNull()} Bar (${(grid.input.pressureLoss.maxOrNull()?:0.0) * 100_000 / grid.criticalPath.sumOf { it.length }} Pa/m)")
        }

    }

    /**
     * Überprüft mehrere Rohrtypen für eine Rohrleitung.
     * Sobald ein Typ zu niedrigeren Gesamtkosten des gesamten Netzes führt, wird dieser als neuer bester Typ ausgewählt.
     *
     * Die Optimierung folgt hierbei folgenden Annahmen:
     * - Der optimale Durchmesser ist immer mindestens genauso groß wie der größte an der Rohrleitung angeschlossene Durchmesser.
     * - Ist der alte Rohrtyp undefiniert, müssen alle Möglichkeiten ausprobiert werden - unabhängig der sonstigen Parameter.
     * - Die aktuell ausgewählte Rohrleitung kann keine bessere Rohrleitung sein und wird deshalb nicht geprüft.
     *   (Falls dies doch gewünscht ist, entsprechend `types` füllen.)
     * - ...
     *
     * @param pipe Pipe - Rohrleitung, die optimiert werden soll.
     * @param types List<PipeType> - Liste möglicher Rohrtypen, die für die Rohrleitung ausprobiert werden können. (Muss nach Durchmesser aufsteigend sortiert sein.)
     * @param skipIfGettingWorse Boolean - Bricht den Test weiterer Typen ab, sobald eine bessere Rohrleitung gefunden wurde und danach schlechtere Ergebnisse erzielt werden.
     * @param skipSmallerThenCurrent Boolean - Überspringt Rohre, die einen kleineren Rohrdurchmesser als die aktuell beste Leitung haben.
     * @return Boolean - Wahr, wenn besserer Rohrtyp gefunden wurde.
     */
    internal fun optimizePipe(
        pipe: Pipe,
        types: List<PipeType> = investParams.pipeTypes.filterNot { it == pipe.type },
        skipIfGettingWorse: Boolean = true,
        skipSmallerThenCurrent: Boolean = false
    ): Boolean {
        val lastTypeWasUndefined = pipe.type == PipeType.UNDEFINED
        var bestType = pipe.type
        var foundBetterType = false

        for (type in types) {

            // if diameter is smaller than largest connected pipe continue. This can't be good.
            if (type.diameter < pipe.target.largestConnectedPipe?.diameter ?: 0.0)
                continue

            if (skipSmallerThenCurrent && type.diameter < bestType.diameter)
                continue

            numberOfTypeChecks++
            pipe.type = type

            val newCost = investParams.calculateCosts(grid)
            if (newCost.totalPerYear < gridCosts.totalPerYear || bestType == PipeType.UNDEFINED) {
                gridCosts = newCost
                bestType = type
                foundBetterType = true
            } else if (foundBetterType && skipIfGettingWorse && !lastTypeWasUndefined)
                break
        }

        if (foundBetterType)
            numberOfUpdates++
        pipe.type = bestType
        return foundBetterType
    }

    /**
     * Optimiert mehrere Rohrleitungen auf einmal. Ist notwendig, um komplexe Zusammenhänge zu erkennen. Oder so.
     *
     * Damit die Methode sauber funktioniert, müssen die Rohre nach der Distanz in Anzahl Rohrleitungen zum Einspeisepunkt
     * absteigend sortiert werden.
     *
     * Es werden für die Rohre 1..n-1 verschiedene Rohrdurchmesser ausprobiert.
     * Für das letzte Rohr n wird die Methode [optimizePipe] aufgerufen.
     *
     * Es wird auch die Möglichkeit des gleichbleibenden Rohrdurchmessers überprüft.
     *
     * @param pipes List<Pipe> - Die zu optimierenden Rohrleitungen.
     * @param types List<PipeType> - Liste möglicher Rohrtypen, die für die Rohrleitung ausprobiert werden können. (Muss nach Durchmesser aufsteigend sortiert sein.)
     * @param skipIfGettingWorse Boolean - Bricht den Test weiterer Typen ab, sobald eine bessere Rohrleitung gefunden wurde und danach schlechtere Ergebnisse erzielt werden.
     * @param skipSmallerThenCurrent Boolean - Überspringt Rohre, die einen kleineren Rohrdurchmesser als die aktuell beste Leitung haben.
     * @param currentIdx Int - Index der Pipe bei der die Optimierung begonnen werden soll.
     * @return Boolean - Wahr, wenn bessere Kombination aus Rohrtypen gefunden wurde.
     */
    internal fun optimizePipes(
        pipes: List<Pipe>,
        types: List<PipeType> = investParams.pipeTypes,
        skipIfGettingWorse: Boolean = true,
        skipSmallerThenCurrent: Boolean = false,
        currentIdx: Int = 0
    ): Boolean {
        val currentPipe = pipes[currentIdx]

        // Optimize last pipe with the other fancy method
        if (currentIdx == pipes.size - 1)
            return optimizePipe(currentPipe, types, skipIfGettingWorse)

        val lastTypeWasUndefined = currentPipe.type == PipeType.UNDEFINED
        var bestType = currentPipe.type
        var betterTypeFound = false

        for (type in investParams.pipeTypes) {

            // if diameter is smaller than largest connected pipe continue. This can't be good.
            if (type.diameter < currentPipe.target.largestConnectedPipe?.diameter ?: 0.0)
                continue

            if (skipSmallerThenCurrent && type.diameter < bestType.diameter)
                continue

            currentPipe.type = type

            if (optimizePipes(pipes, types, skipIfGettingWorse, skipSmallerThenCurrent, currentIdx + 1)) {
                bestType = type
                betterTypeFound = true
            } else if (betterTypeFound && skipIfGettingWorse && !lastTypeWasUndefined)
                break
        }

        currentPipe.type = bestType
        return betterTypeFound
    }

}