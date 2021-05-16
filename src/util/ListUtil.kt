package de.fhac.ewi.util

import java.util.*

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val iterators = map(List<T>::iterator)
    return IntRange(1, maxOf { it.size }).map { iterators.filter(Iterator<T>::hasNext).map(Iterator<T>::next) }
}

fun <K, V> Map<K, V>.singleValue() = values.single()

fun <T> List<T>.repeatEach(amount: Int) = flatMap { value -> List(amount) { value } }