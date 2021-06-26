package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.OutputNode
import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.PipeType

/**
 * Überprüft alle Kombinationen. (außer wenn der Rohrdurchmesser des angeschlossenen Knotens größer wird.)
 */
object AllCombinations : Strategy {
    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        testAllCombinations(grid.pipes.sortedByDescending { it.target.pathToSource.size })
    }

    private fun Optimizer.testAllCombinations(pipes: List<Pipe>): Boolean {
        val currentPipe = pipes.first()
        val nextPipes = pipes.drop(1)
        if (nextPipes.isEmpty())
            return optimizePipe(currentPipe, fastMode = true)

        var bestType = currentPipe.type
        var betterTypeFound = false

        val largestChild = currentPipe.target.largestConnectedPipe

        for (type in investParams.pipeTypes) {
            if (largestChild != null && type.diameter < largestChild.diameter)
                continue // No need to check - the diameter can not be smaller

            currentPipe.type = type
            if (testAllCombinations(nextPipes)) {
                bestType = type
                betterTypeFound = true
            }
        }
        currentPipe.type = bestType
        return betterTypeFound
    }
}