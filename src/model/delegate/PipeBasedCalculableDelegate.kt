package de.fhac.ewi.model.delegate

import de.fhac.ewi.model.Pipe

abstract class PipeBasedCalculableDelegate<T>() : CalculableDelegate<T>() {

    private val connectedPipes = mutableListOf<Pipe>()

    fun addChild(pipe: Pipe) {
        connectedPipes += pipe
        onPipeConnect(pipe)
    }

    override fun recalculateIndexed(index: Int) = recalculateIndexed(index, connectedPipes)

    abstract fun recalculateIndexed(index: Int, pipes: List<Pipe>): Double
    abstract fun onPipeConnect(pipe: Pipe)
}