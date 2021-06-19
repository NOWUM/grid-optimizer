package de.fhac.ewi.model.delegate

abstract class CalculableDelegate<T> : SubscribableDelegate<T, DoubleArray>() {

    abstract fun recalculateIndexed(index: Int): Double

    open fun recalculate(): DoubleArray = DoubleArray(8760) { recalculateIndexed(it) }

    fun updateValue() {
        setValue(recalculate())
    }

    override fun hasChanged(oldValue: DoubleArray, newValue: DoubleArray) = !(oldValue contentEquals newValue)

    override fun lazyInitialValue() = recalculate()
}