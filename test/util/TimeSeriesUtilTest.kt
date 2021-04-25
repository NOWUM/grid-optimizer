package de.fhac.ewi.util

import org.junit.Test
import kotlin.test.assertEquals

class TimeSeriesUtilTest {

    @Test
    fun loadTemperatureTimeSeriesTest() {
        val series = loadTemperatureTimeSeries()
        assertEquals(1, series.size)
    }
}