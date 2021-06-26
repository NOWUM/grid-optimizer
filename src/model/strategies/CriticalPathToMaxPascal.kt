package de.fhac.ewi.model.strategies

import de.fhac.ewi.model.Optimizer
import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.PipeType

/**
 * Wählt die Rohre des kritischen Pfades so, dass diese in der Begrenzung von xxx Pascal/m bleiben.
 */
object CriticalPathToMaxPascal : Strategy {

    const val MAX_PASCAL_PER_METER = 600

    override fun apply(optimizer: Optimizer): Unit = with(optimizer) {
        val pipes = grid.criticalPath.toList()
        pathToMaxPascal(pipes, investParams.pipeTypes)
    }

    private fun pathToMaxPascal(pipes: List<Pipe>, preselectedTypes: List<PipeType>, pathLength: Double = pipes.sumOf { it.length }): Boolean {
        val currentPipe = pipes.first()

        // Weitere Eingrenzung der möglichen Rohrtypen, die für die Optimierung sinnvoll erscheinen
        val possibleTypes = preselectedTypes.toMutableList()
        // Current pipe type musst be larger then largest pipe connected
        currentPipe.target.largestConnectedPipe?.let { largestConnectedPipe ->
            possibleTypes.removeIf { it.diameter < largestConnectedPipe.diameter }
        }
        // If pipe type already set only check bigger or equal types then currently set
        if (currentPipe.type != PipeType.UNDEFINED)
            possibleTypes.removeIf { it.diameter < currentPipe.type.diameter }

        val oldType = currentPipe.type
        val newPipePath = pipes.drop(1)

        for (type in possibleTypes) {
            currentPipe.type = type

            // if there is no next pipe check pressure loss on this pipe
            if (newPipePath.isEmpty()) {
                if (checkPressureLoss(currentPipe, pathLength))
                    return true

            // if the current pressure loss per meter not already over limit check types for next pipe
            } else if (!checkPressureLoss(newPipePath.last(), pathLength) && pathToMaxPascal(newPipePath, possibleTypes, pathLength)) {
                return true
            }
        }

        currentPipe.type = oldType
        return false
    }

    private fun checkPressureLoss(lastPipe: Pipe, pathLength: Double) =
        (lastPipe.totalPressureLoss.maxOrNull()?:0.0) * 100_000 / pathLength <= MAX_PASCAL_PER_METER
}