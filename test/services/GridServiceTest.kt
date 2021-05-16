package de.fhac.ewi.services

import de.fhac.ewi.dto.GridRequest
import org.junit.Test

class GridServiceTest {

    @Test
    fun createGridByEmptyRequest() {
        val service = GridService(
            HeatDemandService(TemperatureTimeSeriesService(emptyList()), LoadProfileService(emptyList())),
            TemperatureTimeSeriesService(emptyList())
        )
        val request = GridRequest("", emptyList(), emptyList(), emptyList(), emptyList())
        service.createByGridRequest(request)
    }
}