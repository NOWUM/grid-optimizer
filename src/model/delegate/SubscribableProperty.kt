package de.fhac.ewi.model.delegate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Self implemented Observable that allows multiple subscriptions.
 *
 * @param T - Keine Ahnung
 * @param V : Any - Datentyp
 * @property value V - Variable die den Wert speichert
 * @property subscribers MutableList<Function0<Unit>> - Subscriber, die bei Änderung benachrichtigt werden
 * @constructor
 */
open class SubscribableProperty<T, V : Any>(initialValue: V? = null) : ReadWriteProperty<T, V> {

    private lateinit var value: V

    val subscribers: MutableList<Subscriber> = mutableListOf()

    init {
        if (initialValue != null)
            value = initialValue
    }

    fun subscribe(subscriber: Subscriber) {
        subscribers += subscriber
    }

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (!this::value.isInitialized)
            setValue(lazyInitialValue())
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
            subscribers.forEach(Subscriber::onValueChange)
    }
}