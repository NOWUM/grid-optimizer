package de.fhac.ewi.model

import org.junit.Test

class IntermediateNodeTest {

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithEmptyId() {
        IntermediateNode("")
    }

    @Test
    fun createOutputNodeWithValidInput() {
        IntermediateNode("1")
    }


    @Test
    fun shouldAllowChildConnection() {
        val inter = IntermediateNode("1")
        val pipe = Pipe("pipe1", inter, IntermediateNode("2"), 10.0, 0.25)
        pipe.source.connectChild(pipe)
    }

    @Test
    fun shouldAllowMultipleChildConnections() {
        val inter = IntermediateNode("1")
        val pipe = Pipe("pipe1", inter, IntermediateNode("2"), 10.0, 0.25)
        val pipe2 = Pipe("pipe2", inter, IntermediateNode("3"), 10.0, 0.25)
        pipe.source.connectChild(pipe)
        pipe2.source.connectChild(pipe2)
    }

    @Test
    fun shouldAcceptOneParentConnection() {
        val inter = IntermediateNode("1")
        val pipe = Pipe("pipe1", IntermediateNode("2"), inter, 10.0, 0.25)
        pipe.source.connectChild(pipe)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailWithTwoParentConnections() {
        val inter = IntermediateNode("1")
        val pipe = Pipe("pipe1", IntermediateNode("2"), inter, 10.0, 0.25)
        val pipe2 = Pipe("pipe1", IntermediateNode("3"), inter, 10.0, 0.25)
        pipe.source.connectChild(pipe)
        pipe2.source.connectChild(pipe2)
    }
}