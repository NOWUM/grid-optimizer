package de.fhac.ewi.services

import de.fhac.ewi.dto.GridRequest
import org.junit.Test

class GridServiceTest {

    @Test
    fun createGridByEmptyRequest() {
        val service = GridService(LoadProfileService(emptyList()))
        val request = GridRequest(emptyList(), emptyList(), emptyList(), emptyList())
        service.createByGridRequest(request)
    }
}