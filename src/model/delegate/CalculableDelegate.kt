package de.fhac.ewi.model.delegate

/**
 * Extension of SubscribableDelegate that allows calculation of DoubleArrays with exactly 8760 elements.
 *
 * @param T - Keine Ahnung
 */
abstract class CalculableDelegate<T> : SubscribableDelegate<T, DoubleArray>() {

    abstract fun recalculateIndexed(index: Int): Double

    open fun recalculate(): DoubleArray = DoubleArray(8760) { recalculateIndexed(it) }

    fun updateValue() {
        setValue(recalculate())
    }

    override fun hasChanged(oldValue: DoubleArray, newValue: DoubleArray) = !(oldValue contentEquals newValue)

    override fun lazyInitialValue() = recalculate()
}