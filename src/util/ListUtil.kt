package de.fhac.ewi.util

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val iterators = map(List<T>::iterator)
    return IntRange(1, maxOf { it.size }).map { iterators.filter(Iterator<T>::hasNext).map(Iterator<T>::next) }
}
