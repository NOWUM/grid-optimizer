package de.fhac.ewi.model.delegate

import kotlin.reflect.KProperty

abstract class LazyCalculableProperty<T, V : Any> : SubscribableProperty<T, V>(), LazySubscriber {

    private var possibleUpdate = false
    private var recalculate = false

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        updateIfNeeded()
        return super.getValue(thisRef, property)
    }

    abstract fun checkForChanges()

    abstract fun recalculate(): V

    override fun lazyInitialValue() = recalculate()

    fun updateIfNeeded() {
        if (possibleUpdate && !recalculate) {
            checkForChanges()
            possibleUpdate = false // we checked - if there must be an update then recalculate now should be set otherwise we dont need to check again
        }
        if (recalculate)
            updateValue()
    }

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
        // Inform subscribers
        for (subscriber in subscribers)
            if (subscriber is LazySubscriber)
                subscriber.onPossibleUpdate() // Lazy subscribers will get possible update information
            else
                subscriber.onValueChange() // we dont know if value will change. For normal subscribers we should force the update
    }

    /**
     * Gets triggered if this value needs to be recalculated.
     */
    override fun onValueChange() {
        if (recalculate)
            return // recalculation already triggered

        recalculate = true
        onPossibleUpdate() // The value of this property can be changed with next get
    }
}