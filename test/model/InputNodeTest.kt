package de.fhac.ewi.model

import de.fhac.ewi.util.toDoubleFunction
import org.junit.Test

class InputNodeTest {

    val GROUND_TEMP_SERIES = List(365){10.0}

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithEmptyId() {
        InputNode("", GROUND_TEMP_SERIES, "42".toDoubleFunction(), "42".toDoubleFunction())
    }

    @Test
    fun createInputNodeWithValidId() {
        InputNode("1", GROUND_TEMP_SERIES, "42".toDoubleFunction(), "42".toDoubleFunction())
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailConnectAsTarget() {
        val pipe =
            Pipe("pipe1", IntermediateNode("1"), InputNode("2", GROUND_TEMP_SERIES, "42".toDoubleFunction(), "42".toDoubleFunction()), 10.0, 0.25)
        pipe.source.connectChild(pipe)
    }


    @Test
    fun shouldAllowOneChildConnection() {
        val pipe =
            Pipe("pipe1", InputNode("1", GROUND_TEMP_SERIES, "42".toDoubleFunction(), "42".toDoubleFunction()), IntermediateNode("2"), 10.0, 0.25)
        pipe.source.connectChild(pipe)
    }


    @Test
    fun shouldAllowTwoChildConnection() {
        val pipe =
            Pipe("pipe1", InputNode("1", GROUND_TEMP_SERIES, "42".toDoubleFunction(), "42".toDoubleFunction()), IntermediateNode("2"), 10.0, 0.25)
        val pipe2 = Pipe("pipe1", pipe.source, IntermediateNode("3"), 10.0, 0.25)
        pipe.source.connectChild(pipe)
        pipe2.source.connectChild(pipe2)
    }
}