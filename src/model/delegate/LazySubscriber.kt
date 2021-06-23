package de.fhac.ewi.model.delegate

interface LazySubscriber: Subscriber {
    fun onPossibleUpdate()
}