package de.fhac.ewi.model

import org.junit.Test

class PipeTest {

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnPipeWithEmptyId() {
        Pipe("", IntermediateNode("#1"), IntermediateNode("#2"), 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnPipeWithNegativeLength() {
        Pipe("P1", IntermediateNode("#1"), IntermediateNode("#2"), -10.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnPipeWithZeroLength() {
        Pipe("P1", IntermediateNode("#1"), IntermediateNode("#2"), 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnPipeWithSameSourceAndTarget() {
        val node = IntermediateNode("#1")
        Pipe("P1", node, node, 0.0)
    }

    @Test
    fun createPipe() {
        Pipe("P1", IntermediateNode("#1"), IntermediateNode("#2"), 42.0)
    }
}