package de.fhac.ewi.model

import de.fhac.ewi.util.flowRate
import de.fhac.ewi.util.pipeHeatLoss
import de.fhac.ewi.util.pipePressureLoss
import de.fhac.ewi.util.volumeFlow

data class Pipe(
    val id: String,
    val source: Node,
    val target: Node,
    val length: Double, // in m
    val coverageHeight: Double // in m
) {

    var type: PipeType = PipeType.UNDEFINED

    val volumeFlow: List<Double>
        get() = target.connectedThermalEnergyDemand.curve.mapIndexed { idx, energyDemand ->
            volumeFlow(source.flowInTemperature[idx], source.flowOutTemperature[idx], energyDemand)
        }

    // Strömungsgeschwindigkeit = Volumenstrom / Rohrquerschnittsfläche
    val flowRate: List<Double>
        get() = volumeFlow.map { flowRate(type.diameter, it) }

    val pipePressureLoss: List<Double>
        get() = flowRate.map { pipePressureLoss(it, length, type.diameter) }

    val pipeHeatLoss: List<Double>
        get() {
            val flowIn = source.flowInTemperature
            val flowOut = source.flowOutTemperature
            val ground = source.groundTemperature
            return flowIn.indices.map { idx -> pipeHeatLoss(flowIn[idx], flowOut[idx], ground[idx],
                type.diameter, type.isolationThickness, coverageHeight, type.distanceBetweenPipes, length) }
        }

    init {
        if (id.isBlank())
            throw IllegalArgumentException("Id of pipe must be filled.")

        if (source == target)
            throw IllegalArgumentException("Source and target node of pipe can not be the same.")

        if (length <= 0.0)
            throw IllegalArgumentException("Length of pipe can not be negative or zero.")

        if (coverageHeight < 0.0)
            throw IllegalArgumentException("Coverage height can not be negative.")
    }

}
