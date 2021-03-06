package de.fhac.ewi.model.delegate

/**
 * Extension of SubscribableDelegate that allows calculation of DoubleArrays with exactly 8760 elements.
 *
 * @param T - Keine Ahnung
 */
abstract class LazyCalculableDoubleArray<T> : LazyCalculableProperty<T, DoubleArray>() {

    override fun hasChanged(oldValue: DoubleArray, newValue: DoubleArray) = !(oldValue contentEquals newValue)

}