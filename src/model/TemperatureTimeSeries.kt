package de.fhac.ewi.model

data class TemperatureTimeSeries(val key: String, val temperatures: List<Double>) {

    init {
        if (key.isBlank()) throw IllegalArgumentException("The key for the temperature time series can not be empty.")
        if (temperatures.size != 365) throw IllegalArgumentException("The time series must contain exactly 365 temperatures.")
    }
}
