package de.fhac.ewi.model.delegate

import kotlin.reflect.KProperty

abstract class LazyCalculableProperty<T, V : Any> : SubscribableProperty<T, V>(), LazySubscriber {

    var possibleUpdate = false
    var recalculate = false

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (possibleUpdate && !recalculate) {
            checkForChanges()
            possibleUpdate = false // we checked - if there must be an update then recalculate now should be set
        }
        if (recalculate)
            updateValue()
        return super.getValue(thisRef, property)
    }

    abstract fun checkForChanges()

    abstract fun recalculate(): V

    override fun lazyInitialValue() = recalculate()

    private fun updateValue() {
        setValue(recalculate())
        recalculate = false
        possibleUpdate = false
    }

    /**
     * Gets triggered if there might be an update for this value.
     */
    override fun onPossibleUpdate() {
        if (possibleUpdate)
            return // possible update already triggered

        possibleUpdate = true
        subscribers.filterIsInstance<LazySubscriber>().forEach(LazySubscriber::onPossibleUpdate)
    }

    /**
     * Gets triggered if this value needs to be recalculated.
     */
    override fun onValueChange() {
        if (recalculate)
            return // recalculation already triggered

        recalculate = true
        if (!possibleUpdate) // if possible update not already announced
            subscribers.filterIsInstance<LazySubscriber>().forEach(LazySubscriber::onPossibleUpdate)
        possibleUpdate = false // possible update now false, because we already forced an recalculation
    }
}