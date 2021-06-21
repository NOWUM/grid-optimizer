package de.fhac.ewi.util

import de.fhac.ewi.model.Pipe
import de.fhac.ewi.model.delegate.PipeBasedCalculableDelegate
import de.fhac.ewi.model.delegate.SubscribableDelegate
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible


inline fun <reified R> KProperty0<*>.delegateAs(): R? {
    isAccessible = true
    return getDelegate() as? R
}

fun KProperty0<*>.subscribeIfChanged(onChange: () -> Unit) {
    delegateAs<SubscribableDelegate<*, *>>()?.subscribe(onChange)
}

fun KProperty0<*>.addPipeIfNeeded(pipe: Pipe) {
    delegateAs<PipeBasedCalculableDelegate<*>>()?.addChild(pipe)
}