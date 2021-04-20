package de.fhac.ewi.services

import de.fhac.ewi.dto.PipeRequest
import de.fhac.ewi.model.InputNode
import de.fhac.ewi.model.IntermediateNode
import de.fhac.ewi.model.LoadProfile
import de.fhac.ewi.model.OutputNode
import org.junit.Test

class GridServiceConnectTest {

    @Test
    fun connectInputNodeWithOutputNode() {
        val source = InputNode("#1")
        val target = OutputNode("#2", 0.0, 0.0, LoadProfile.SLP)
        val pipe = PipeRequest("|1", "#1", "#2", 0.0)
        GridService().connect(source, target, pipe)
    }

    @Test
    fun connectIntermediateNodeWithOutputNode() {
        val source = IntermediateNode("#1")
        val target = OutputNode("#2", 0.0, 0.0, LoadProfile.SLP)
        val pipe = PipeRequest("|1", "#1", "#2", 0.0)
        GridService().connect(source, target, pipe)
    }

    @Test
    fun connectIntermediateNodeWithIntermediateNode() {
        val source = IntermediateNode("#1")
        val target = OutputNode("#2", 0.0, 0.0, LoadProfile.SLP)
        val pipe = PipeRequest("|1", "#1", "#2", 0.0)
        GridService().connect(source, target, pipe)
    }

    @Test
    fun connectTwoIntermediateNodeWithOutputNode() {
        val source = IntermediateNode("#1")
        val source2 = IntermediateNode("#2")
        val target = OutputNode("#3", 0.0, 0.0, LoadProfile.SLP)
        val pipe = PipeRequest("|1", "#1", "#3", 0.0)
        val pipe2 = PipeRequest("|2", "#2", "#3", 0.0)
        val service = GridService()
        service.connect(source, target, pipe)
        service.connect(source2, target, pipe2)
    }

    @Test
    fun connectTwoIntermediateNodeWithIntermediateNode() {
        val source = IntermediateNode("#1")
        val source2 = IntermediateNode("#2")
        val target = IntermediateNode("#3")
        val pipe = PipeRequest("|1", "#1", "#3", 0.0)
        val pipe2 = PipeRequest("|2", "#2", "#3", 0.0)
        val service = GridService()
        service.connect(source, target, pipe)
        service.connect(source2, target, pipe2)
    }

    @Test(expected = IllegalArgumentException::class)
    fun connectInputNodeWithInputNode() {
        val source = InputNode("#1")
        val target = InputNode("#2")
        val pipe = PipeRequest("|1", "#1", "#2", 0.0)
        GridService().connect(source, target, pipe)
    }

    @Test(expected = IllegalArgumentException::class)
    fun connectOutputNodeWithOutputNode() {
        val source = OutputNode("#1", 0.0, 0.0, LoadProfile.SLP)
        val target = OutputNode("#2", 0.0, 0.0, LoadProfile.SLP)
        val pipe = PipeRequest("|1", "#1", "#2", 0.0)
        GridService().connect(source, target, pipe)
    }

}