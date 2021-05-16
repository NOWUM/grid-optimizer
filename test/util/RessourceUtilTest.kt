package de.fhac.ewi.util

import org.junit.Test
import kotlin.test.assertEquals

class RessourceUtilTest {

    @Test
    fun loadTemperatureTimeSeriesTest() {
        val series = loadTemperatureTimeSeries()
        assertEquals(4, series.size)
    }

    @Test
    fun checkLoadProfilesFolder() {
        val files = getHProfileNames()
        assertEquals(2, files.size)
    }

    @Test
    fun loadHProfilesTest() {
        val profiles = loadHProfiles()
        assertEquals(2, profiles.size)
    }
}