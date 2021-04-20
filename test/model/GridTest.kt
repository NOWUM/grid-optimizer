package de.fhac.ewi.model

import org.junit.Test

class GridTest {

    @Test
    fun createEmptyGrid() {
        Grid()
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnInputNodeWithEmptyId() {
        val grid = Grid()
        grid.addInputNode("")
    }

    @Test
    fun addInputNode() {
        val grid = Grid()
        grid.addInputNode("1")
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnTwoInputNodesWithSameId() {
        val grid = Grid()
        grid.addInputNode("1")
        grid.addInputNode("1")
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnTwoInputNodesWithDifferentId() {
        val grid = Grid()
        grid.addInputNode("1")
        grid.addInputNode("1")
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
        grid.addOutputNode("", 0.0, 0.0)
    }

    @Test
    fun addOutputNode() {
        val grid = Grid()
        grid.addOutputNode("1", 10.0, 1.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldFailOnTwoOutputNodesWithSameId() {
        val grid = Grid()
        grid.addOutputNode("1", 10.0, 1.0)
        grid.addOutputNode("1", 10.0, 1.0)
    }

    @Test
    fun acceptTwoOutputNodesWithDifferentId() {
        val grid = Grid()
        grid.addOutputNode("1", 10.0, 1.0)
        grid.addOutputNode("2", 10.0, 1.0)
    }
}