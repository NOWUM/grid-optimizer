package de.fhac.ewi.model

import org.junit.Test

class InputNodeTest {

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithEmptyId() {
        InputNode("")
    }

    @Test
    fun createInputNodeWithValidId() {
        InputNode("1")
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailConnectAsTarget() {
        val pipe = Pipe("pipe1", IntermediateNode("1"), InputNode("2"), 10.0)
        pipe.source.connectChild(pipe)
    }


    @Test
    fun shouldAllowOneChildConnection() {
        val pipe = Pipe("pipe1", InputNode("1"), IntermediateNode("2"), 10.0)
        pipe.source.connectChild(pipe)
    }


    @Test
    fun shouldAllowTwoChildConnection() {
        val pipe = Pipe("pipe1", InputNode("1"), IntermediateNode("2"), 10.0)
        val pipe2 = Pipe("pipe1", pipe.source, IntermediateNode("3"), 10.0)
        pipe.source.connectChild(pipe)
        pipe2.source.connectChild(pipe2)
    }
}