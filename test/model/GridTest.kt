package de.fhac.ewi.model

import de.fhac.ewi.util.toDoubleFunction
import org.junit.Test

class GridTest {


    private val SIMPLE_HEAT_DEMAND = HeatDemandCurve(List(8760) { 1.0 })
    
    @Test
    fun createEmptyGrid() {
        Grid()
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnInputNodeWithEmptyId() {
        val grid = Grid()
        grid.addInputNode("", "42".toDoubleFunction(), "42".toDoubleFunction())
    }

    @Test
    fun addInputNode() {
        val grid = Grid()
        grid.addInputNode("1", "42".toDoubleFunction(), "42".toDoubleFunction())
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnTwoInputNodesWithSameId() {
        val grid = Grid()
        grid.addInputNode("1", "42".toDoubleFunction(), "42".toDoubleFunction())
        grid.addInputNode("1", "42".toDoubleFunction(), "42".toDoubleFunction())
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnTwoInputNodesWithDifferentId() {
        val grid = Grid()
        grid.addInputNode("1", "42".toDoubleFunction(), "42".toDoubleFunction())
        grid.addInputNode("2", "42".toDoubleFunction(), "42".toDoubleFunction())
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnIntermediateNodeWithEmptyId() {
        val grid = Grid()
        grid.addIntermediateNode("")
    }

    @Test
    fun addIntermediateNode() {
        val grid = Grid()
        grid.addIntermediateNode("1")
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnTwoIntermediateNodesWithSameId() {
        val grid = Grid()
        grid.addIntermediateNode("1")
        grid.addIntermediateNode("1")
    }

    @Test
    fun acceptTwoIntermediateNodesWithDifferentId() {
        val grid = Grid()
        grid.addIntermediateNode("1")
        grid.addIntermediateNode("2")
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnOutputNodeWithEmptyId() {
        val grid = Grid()
        grid.addOutputNode("", HeatDemandCurve.ZERO, 0.0)
    }

    @Test
    fun addOutputNode() {
        val grid = Grid()
        grid.addOutputNode("1", SIMPLE_HEAT_DEMAND, 1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnTwoOutputNodesWithSameId() {
        val grid = Grid()
        grid.addOutputNode("1", SIMPLE_HEAT_DEMAND, 1.0)
        grid.addOutputNode("1", SIMPLE_HEAT_DEMAND, 1.0)
    }

    @Test
    fun acceptTwoOutputNodesWithDifferentId() {
        val grid = Grid()
        grid.addOutputNode("1", SIMPLE_HEAT_DEMAND, 1.0)
        grid.addOutputNode("2", SIMPLE_HEAT_DEMAND, 1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnCircularPipes() {
        val grid = Grid()
        grid.addIntermediateNode("1")
        grid.addIntermediateNode("2")
        grid.addPipe("P1", "1", "2", 10.0)
        grid.addPipe("P2", "2", "1", 10.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnTwoPipesWithSameId() {
        val grid = Grid()
        grid.addIntermediateNode("1")
        grid.addIntermediateNode("2")
        grid.addIntermediateNode("3")
        grid.addPipe("P1", "1", "2", 10.0)
        grid.addPipe("P1", "2", "3", 10.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnTwoPipesWithSameConnections() {
        val grid = Grid()
        grid.addIntermediateNode("1")
        grid.addIntermediateNode("2")
        grid.addPipe("P1", "1", "2", 10.0)
        grid.addPipe("P2", "1", "2", 10.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnPipeConnectingNodeWithItself() {
        val grid = Grid()
        grid.addIntermediateNode("1")
        grid.addPipe("P1", "1", "1", 10.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnComplexCircularPipes() {
        val grid = Grid()
        grid.addIntermediateNode("1")
        grid.addIntermediateNode("2")
        grid.addIntermediateNode("3")
        grid.addPipe("P1", "1", "2", 10.0)
        grid.addPipe("P2", "2", "3", 10.0)
        grid.addPipe("P3", "3", "1", 10.0)
    }

    @Test
    fun acceptValidGrid() {
        val grid = Grid()
        grid.addInputNode("1", "42".toDoubleFunction(), "42".toDoubleFunction())
        grid.addIntermediateNode("2")
        grid.addOutputNode("3", SIMPLE_HEAT_DEMAND, 1.0)
        grid.addPipe("P1", "1", "2", 10.0)
        grid.addPipe("P2", "2", "3", 10.0)
        grid.validate()
    }
}