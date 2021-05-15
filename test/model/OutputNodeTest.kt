package de.fhac.ewi.model

import org.junit.Test

class OutputNodeTest {

    private val SIMPLE_HEAT_DEMAND = HeatDemandCurve(List(8760) { 1.0 })

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithEmptyId() {
        OutputNode("", HeatDemandCurve.ZERO, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithEmptyThermalEnergyDemand() {
        OutputNode("1", HeatDemandCurve.ZERO, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithNegativeThermalEnergyDemand() {
        OutputNode("1", HeatDemandCurve(List(365) { -1.0 }), 0.0)
    }


    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithNegativePressureLoss() {
        OutputNode("1", SIMPLE_HEAT_DEMAND, -1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithEmptyPressureLoss() {
        OutputNode("1", SIMPLE_HEAT_DEMAND, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithHighPressureLoss() {
        OutputNode("1", SIMPLE_HEAT_DEMAND, 10.0)
    }

    @Test
    fun createOutputNodeWithValidInput() {
        OutputNode("1", SIMPLE_HEAT_DEMAND, 1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnChildConnection() {
        val out = OutputNode("1", SIMPLE_HEAT_DEMAND, 1.0)
        val pipe = Pipe("pipe1", out, IntermediateNode("2"), 10.0)
        pipe.source.connectChild(pipe)
    }

    @Test
    fun shouldAcceptOneParentConnection() {
        val out = OutputNode("1", SIMPLE_HEAT_DEMAND, 1.0)
        val pipe = Pipe("pipe1", IntermediateNode("2"), out, 10.0)
        pipe.source.connectChild(pipe)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithTwoParentConnections() {
        val out = OutputNode("1", SIMPLE_HEAT_DEMAND, 1.0)
        val pipe = Pipe("pipe1", IntermediateNode("2"), out, 10.0)
        val pipe2 = Pipe("pipe1", IntermediateNode("3"), out, 10.0)
        pipe.source.connectChild(pipe)
        pipe2.source.connectChild(pipe2)
    }
}