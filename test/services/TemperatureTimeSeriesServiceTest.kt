package de.fhac.ewi.services

import de.fhac.ewi.model.TemperatureTimeSeries
import org.junit.Test
import kotlin.test.assertEquals

class TemperatureTimeSeriesServiceTest {

    @Test
    fun checkKeys() {
        val series = listOf(TemperatureTimeSeries("Foo", List(365){0.0}))
        val service = TemperatureTimeSeriesService(series)
        val keys = service.getAllKeys()
        assertEquals(1, keys.size)
        assertEquals("Foo", keys.first())
    }


    @Test
    fun getSeries() {
        val series = listOf(TemperatureTimeSeries("Foo", List(365){0.0}))
        val service = TemperatureTimeSeriesService(series)
        val requested = service.getSeries("Foo")
        assertEquals(series.first(), requested)
    }
}