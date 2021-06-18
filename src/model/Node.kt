package de.fhac.ewi.model

import de.fhac.ewi.util.mapIndicesParallel
import de.fhac.ewi.util.mapParallel
import de.fhac.ewi.util.neededPumpPower

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
            // TODO Pipe Pressure Loss counts twice because hin und rückweg
            val losses = connectedPipes.filter { it.source == this }
                .map { pipe -> pipe.pipePressureLoss.zip(pipe.target.connectedPressureLoss).mapParallel { (a, b) -> a + b } }

            if (losses.isEmpty()) return List(8760) { 0.0 }

            // calculate maximum pressure loss for each hour
            return losses.first().mapIndicesParallel { idx -> losses.maxOf { it[idx] } }
        }

    open val neededPumpPower: List<Double>
        get() {
            // (Druckverlust im Rohr + daran angeschlossener höchster Druckverlust) * 100_000 [Umrechnung Bar zu Pascal]  * Volumenstrom
            // TODO Pipe Pressure Loss counts twice because hin und rückweg
            val powers = connectedPipes.filter { it.source == this }
                .map { pipe ->
                    pipe.pipePressureLoss.zip(pipe.target.connectedPressureLoss).mapParallel { (a, b) -> a + b }
                        .zip(pipe.volumeFlow)
                        .mapParallel { (pressureLoss, volumeFlow) -> neededPumpPower(pressureLoss, volumeFlow) }
                }

            if (powers.isEmpty()) return List(8760) { 0.0 }

            // calculate maximum needed pump power for each hour
            return powers.first().mapIndicesParallel { idx -> powers.maxOf { it[idx] } }
        }

    open val connectedThermalEnergyDemand: HeatDemandCurve
        get() = connectedPipes.filter { it.source == this }
            .fold(HeatDemandCurve.ZERO) { r, p -> r + p.target.connectedThermalEnergyDemand }

    open val flowInTemperature: List<Double>
        get() = connectedPipes.single { it.target == this }.source.flowInTemperature // TODO Wärmeverlust der Pipe berücksichtigen

    open val flowOutTemperature: List<Double>
        get() = connectedPipes.single { it.target == this }.source.flowOutTemperature // TODO Wärmeverlust der Pipe berücksichtigen

    open val groundTemperature: List<Double>
        get() = connectedPipes.single { it.target == this }.source.groundTemperature

    fun isParentOf(target: Node): Boolean =
        target in connectedChildNodes || connectedChildNodes.any { it.isParentOf(target) }

    open fun canReceiveInputFrom(source: Node) = !isParentOf(source) && connectedParentNodes.isEmpty()

    open fun connectChild(pipe: Pipe) {
        if (pipe.source != this)
            throw IllegalArgumentException("Source of ${pipe.id} does not match ${this.id}.")

        if (isParentOf(pipe.target))
            throw IllegalArgumentException("${this.id} is already parent of ${pipe.target.id}")

        if (!pipe.target.canReceiveInputFrom(this))
            throw IllegalArgumentException("${pipe.target.id} can not receive input from ${this.id}.")

        connectedPipes += pipe
        pipe.target.connectedPipes += pipe
    }

}