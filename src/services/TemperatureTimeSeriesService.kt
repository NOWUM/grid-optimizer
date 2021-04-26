package de.fhac.ewi.services

import de.fhac.ewi.model.TemperatureTimeSeries

class TemperatureTimeSeriesService(private val timeSeries: List<TemperatureTimeSeries>) {

    fun getAllKeys() = timeSeries.map { it.key }

    fun getSeries(key: String) = timeSeries.firstOrNull { it.key == key }
        ?: throw IllegalArgumentException("No temperature time series found for key $key")
}