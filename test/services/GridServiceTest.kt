package de.fhac.ewi.services

import de.fhac.ewi.dto.GridRequest
import org.junit.Test

class GridServiceTest {

    @Test
    fun createGridByEmptyRequest() {
        val service = GridService()
        val request = GridRequest(emptyList(), emptyList(), emptyList(), emptyList())
        val grid = service.createByGridRequest(request)
    }
}