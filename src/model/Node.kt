package de.fhac.ewi.model

abstract class Node(val id: String) {

    private val connectedPipes = mutableListOf<Pipe>()

    val connectedChildNodes: List<Node>
        get() = connectedPipes.filter { it.source == this }.map { it.target }

    val connectedParentNodes: List<Node>
        get() = connectedPipes.filter { it.source == this }.map { it.target }

    fun isParentOf(target: Node): Boolean =
        target in connectedChildNodes || connectedChildNodes.any { it.isParentOf(target) }

    open fun canReceiveInputFrom(source: Node) = !isParentOf(source)

    open fun connectChild(pipe: Pipe) {
        if (pipe.source != this)
            throw IllegalStateException("Source of $pipe does not match $this.")

        if(isParentOf(pipe.target))
            throw IllegalStateException("$this is already parent of ${pipe.target}")

        if(!pipe.target.canReceiveInputFrom(this))
            throw IllegalStateException("${pipe.target} can not receive input from $this.")

        connectedPipes += pipe
        pipe.target.connectedPipes += pipe
    }


}