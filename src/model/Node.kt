package de.fhac.ewi.model

abstract class Node(val id: String) {

    init {
        if (id.isBlank())
            throw IllegalArgumentException("Id of node must be filled.")
    }

    private val connectedPipes = mutableListOf<Pipe>()

    val connectedChildNodes: List<Node>
        get() = connectedPipes.filter { it.source == this }.map { it.target }

    val connectedParentNodes: List<Node>
        get() = connectedPipes.filter { it.target == this }.map { it.source }


    // complex attributes
    open val connectedPressureLoss: List<Double>
        get() {
            // Retrieve losses per connected pipe
            val losses = connectedPipes.filter { it.source == this }
                .map { pipe -> pipe.pipePressureLoss.zip(pipe.target.connectedPressureLoss).map { (a, b) -> a + b } }

            if (losses.isEmpty()) return List(8760) { 0.0 }

            // calculate maximum pressure loss for each hour
            return losses.first().indices.map { idx -> losses.maxOf { it[idx] } }
        }

    open val neededPumpPower: List<Double>
        get() {
            // (Druckverlust im Rohr + daran angeschlossener höchster Druckverlust) * Volumenstrom
            val powers = connectedPipes.filter { it.source == this }
                .map { pipe ->
                    pipe.pipePressureLoss.zip(pipe.target.connectedPressureLoss).map { (a, b) -> a + b }
                        .zip(pipe.volumeFlow).map { (a, b) -> a * b }
                }

            if (powers.isEmpty()) return List(8760) { 0.0 }

            // calculate maximum needed pump power for each hour
            return powers.first().indices.map { idx -> powers.maxOf { it[idx] } }
        }

    open val connectedThermalEnergyDemand: HeatDemandCurve
        get() = connectedPipes.filter { it.source == this }
            .fold(HeatDemandCurve.ZERO) { r, p -> r + p.target.connectedThermalEnergyDemand }

    fun isParentOf(target: Node): Boolean =
        target in connectedChildNodes || connectedChildNodes.any { it.isParentOf(target) }

    open fun canReceiveInputFrom(source: Node) = !isParentOf(source) && connectedParentNodes.isEmpty()

    open fun connectChild(pipe: Pipe) {
        if (pipe.source != this)
            throw IllegalArgumentException("Source of $pipe does not match $this.")

        if (isParentOf(pipe.target))
            throw IllegalArgumentException("$this is already parent of ${pipe.target}")

        if (!pipe.target.canReceiveInputFrom(this))
            throw IllegalArgumentException("${pipe.target} can not receive input from $this.")

        connectedPipes += pipe
        pipe.target.connectedPipes += pipe
    }

}