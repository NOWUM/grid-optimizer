package de.fhac.ewi.model.delegate

import de.fhac.ewi.model.Pipe

/**
 * Extension of CalculableDelegate that uses child pipes for calculation.
 *
 * @param T
 * @property connectedPipes MutableList<Pipe> - Child pipes
 */
abstract class PipeBasedCalculableDelegate<T> : LazyCalculableDoubleArray<T>() {

    private val connectedPipes = mutableListOf<Pipe>()

    fun addChild(pipe: Pipe) {
        connectedPipes += pipe
        onPipeConnect(pipe)
    }

    override fun recalculate() = recalculate(connectedPipes)

    abstract fun recalculate(pipes: List<Pipe>): DoubleArray

    // Needed to prevent exception due circular call
    override fun lazyInitialValue() = recalculate()


    abstract fun onPipeConnect(pipe: Pipe)

    abstract fun onPossiblePipeUpdate(pipe: Pipe)

    override fun checkForChanges() {
        connectedPipes.forEach(::onPossiblePipeUpdate)
    }
}