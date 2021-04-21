package de.fhac.ewi.model

import org.junit.Test

class OutputNodeTest {

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithEmptyId() {
        OutputNode("", 0.0, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithEmptyThermalEnergyDemand() {
        OutputNode("1", 0.0, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithNegativeThermalEnergyDemand() {
        OutputNode("1", -2.0, 0.0)
    }


    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithNegativePressureLoss() {
        OutputNode("1", 2.0, -1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithEmptyPressureLoss() {
        OutputNode("1", 2.0, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithHighPressureLoss() {
        OutputNode("1", 2.0, 10.0)
    }

    @Test
    fun createOutputNodeWithValidInput() {
        OutputNode("1", 1.0, 1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnChildConnection() {
        val out = OutputNode("1", 1.0, 1.0)
        val pipe = Pipe("pipe1", out, IntermediateNode("2"), 10.0)
        pipe.source.connectChild(pipe)
    }

    @Test
    fun shouldAcceptOneParentConnection() {
        val out = OutputNode("1", 1.0, 1.0)
        val pipe = Pipe("pipe1", IntermediateNode("2"), out, 10.0)
        pipe.source.connectChild(pipe)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithTwoParentConnections() {
        val out = OutputNode("1", 1.0, 1.0)
        val pipe = Pipe("pipe1", IntermediateNode("2"), out, 10.0)
        val pipe2 = Pipe("pipe1", IntermediateNode("3"), out, 10.0)
        pipe.source.connectChild(pipe)
        pipe2.source.connectChild(pipe2)
    }
}