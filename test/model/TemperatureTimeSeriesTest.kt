package de.fhac.ewi.model

import org.junit.Test

class TemperatureTimeSeriesTest {

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnEmptyKey() {
        TemperatureTimeSeries("", List(365) { 0.0 })
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOn364Elements() {
        TemperatureTimeSeries("foo", List(364) { 0.0 })
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOn366Elements() {
        TemperatureTimeSeries("bar", List(366) { 0.0 })
    }

    @Test
    fun shouldCreateTemperatureTimeSeries() {
        TemperatureTimeSeries("bar", List(365) { 0.0 })
    }
}