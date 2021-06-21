package de.fhac.ewi.model.delegate

import de.fhac.ewi.model.Pipe

/**
 * Extension of CalculableDelegate that uses child pipes for calculation.
 *
 * @param T
 * @property connectedPipes MutableList<Pipe> - Child pipes
 */
abstract class PipeBasedCalculableDelegate<T> : CalculableDelegate<T>() {

    private val connectedPipes = mutableListOf<Pipe>()

    fun addChild(pipe: Pipe) {
        connectedPipes += pipe
        onPipeConnect(pipe)
    }

    override fun recalculateIndexed(index: Int) = recalculateIndexed(index, connectedPipes)

    // Needed to prevent exception due circular call
    override fun lazyInitialValue() = DoubleArray(8760) { 999_999.0 }

    abstract fun recalculateIndexed(index: Int, pipes: List<Pipe>): Double
    abstract fun onPipeConnect(pipe: Pipe)

}