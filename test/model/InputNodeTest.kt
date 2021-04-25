package de.fhac.ewi.model

import de.fhac.ewi.util.toDoubleFunction
import org.junit.Test

class InputNodeTest {

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithEmptyId() {
        InputNode("", "42".toDoubleFunction(), "42".toDoubleFunction())
    }

    @Test
    fun createInputNodeWithValidId() {
        InputNode("1", "42".toDoubleFunction(), "42".toDoubleFunction())
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailConnectAsTarget() {
        val pipe = Pipe("pipe1", IntermediateNode("1"), InputNode("2", "42".toDoubleFunction(), "42".toDoubleFunction()), 10.0)
        pipe.source.connectChild(pipe)
    }


    @Test
    fun shouldAllowOneChildConnection() {
        val pipe = Pipe("pipe1", InputNode("1", "42".toDoubleFunction(), "42".toDoubleFunction()), IntermediateNode("2"), 10.0)
        pipe.source.connectChild(pipe)
    }


    @Test
    fun shouldAllowTwoChildConnection() {
        val pipe = Pipe("pipe1", InputNode("1", "42".toDoubleFunction(), "42".toDoubleFunction()), IntermediateNode("2"), 10.0)
        val pipe2 = Pipe("pipe1", pipe.source, IntermediateNode("3"), 10.0)
        pipe.source.connectChild(pipe)
        pipe2.source.connectChild(pipe2)
    }
}