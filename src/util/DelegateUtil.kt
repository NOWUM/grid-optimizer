package de.fhac.ewi.util

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.*
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible


inline fun <reified R> KProperty0<*>.delegateAs(): R? {
    isAccessible = true
    return getDelegate() as? R
}

fun KProperty0<*>.subscribeIfChanged(subscriber: Subscriber) {
    delegateAs<SubscribableProperty<*, *>>()?.subscribe(subscriber)
}

fun KProperty0<*>.updateIfNeeded() {
    delegateAs<LazyCalculableProperty<*, *>>()?.run { updateIfNeeded() }
}

fun KProperty0<*>.addPipeIfNeeded(pipe: Pipe) {
    delegateAs<PipeBasedCalculableDelegate<*>>()?.addChild(pipe)
}