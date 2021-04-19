package de.fhac.ewi.model

abstract class Node(val id: String) {

    val connectedPipes = mutableListOf<Pipe>()

    val connectedNodes: List<Node>
        get() = connectedPipes.map { it.target }
}