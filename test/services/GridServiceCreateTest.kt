package de.fhac.ewi.services

import de.fhac.ewi.dto.PipeRequest
import de.fhac.ewi.model.InputNode
import de.fhac.ewi.model.LoadProfile
import de.fhac.ewi.model.OutputNode
import org.junit.Test

class GridServiceCreateTest {

    @Test(expected = IllegalArgumentException::class)
    fun createEmptyGrid() {
        GridService().create(emptyList(), emptyList())
    }

    @Test(expected = IllegalArgumentException::class)
    fun createGridWithTwoInput() {
        GridService().create(listOf(InputNode("#1"), InputNode("#2")), emptyList())
    }

    @Test(expected = IllegalArgumentException::class)
    fun createGridWithUnconnectedOutput() {
        GridService().create(listOf(InputNode("#1"), OutputNode("#2", 0.0, 0.0, LoadProfile.SLP)), emptyList())
    }

    @Test
    fun createSimpleGrid() {
        GridService().create(listOf(InputNode("#1"), OutputNode("#2", 0.0, 0.0, LoadProfile.SLP)), listOf(PipeRequest("|1", "#1", "#2", 0.0)))
    }
}