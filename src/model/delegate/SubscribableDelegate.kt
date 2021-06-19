package de.fhac.ewi.model.delegate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class SubscribableDelegate<T, V : Any>(initialValue: V? = null) : ReadWriteProperty<T, V> {

    private lateinit var value: V

    private val subscribers: MutableList<() -> Unit> = mutableListOf()

    init {
        if (initialValue != null)
            value = initialValue
    }

    fun subscribe(onChange: () -> Unit) {
        subscribers += onChange
    }

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (!this::value.isInitialized)
            value = lazyInitialValue()
        return value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) = setValue(value)

    open fun hasChanged(oldValue: V, newValue: V) = oldValue != newValue

    open fun lazyInitialValue(): V =
        throw IllegalStateException("No initial value or lazy initializer provided for property.")

    fun setValue(newValue: V) {
        val oldValue = if (this::value.isInitialized) value else null
        value = newValue

        if (oldValue == null || hasChanged(oldValue, newValue))
            subscribers.forEach { it.invoke() }
    }
}