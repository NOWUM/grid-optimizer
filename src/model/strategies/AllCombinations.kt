package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.OutputNode
import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.PipeType

/**
 * Überprüft alle möglichen Kombinationen.
 * Dazu werden die Rohre nach ihrer Distanz zum Einspeisepunkt absteigend sortiert und jede sinnvolle Kombination getestet.
 * Sinnvoll meint hierbei, dass der Rohrdurchmesser mindestens genauso groß sein muss, wie der größte daran angeschlossene Rohrdurchmesser.
 * Sobald bei dem Versuch ein größeren Rohrdurchmesser einzusetzen ein schlechteres Ergebnis kommt, kann vorzeitig abgebrochen werden.
 */
object AllCombinations : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        grid.pipes.forEach { it.type = investParams.pipeTypes.first() }
        testAllCombinations(grid.pipes.sortedByDescending { it.target.pathToSource.size })
    }

    private fun Optimizer.testAllCombinations(pipes: List<Pipe>, current: Int = 0): Boolean {
        val currentPipe = pipes[current]
        if (current == pipes.size - 1)
            return optimizePipe(currentPipe, investParams.pipeTypes, fastMode = true)

        var bestType = currentPipe.type
        var betterTypeFound = false

        val largestChild = currentPipe.target.largestConnectedPipe

        for (type in investParams.pipeTypes) {
           if (largestChild != null && type.diameter < largestChild.diameter)
               continue // No need to check - the diameter can not be smaller

            currentPipe.type = type
            if (testAllCombinations(pipes, current + 1)) {
                bestType = type
                betterTypeFound = true
            } else if (betterTypeFound)
                break // we are getting worse. Stop checking
        }
        currentPipe.type = bestType
        return betterTypeFound
    }
}