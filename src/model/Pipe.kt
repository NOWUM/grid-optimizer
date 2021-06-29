package de.fhac.ewi.model

import de.fhac.ewi.model.delegate.SubscribableProperty
import de.fhac.ewi.model.math.*

open class Pipe(
    val id: String,
    val source: Node,
    val target: Node,
    val length: Double, // in m
    val coverageHeight: Double // in m
) {

    var type by SubscribableProperty(PipeType.UNDEFINED)

    val heatLoss by PipeHeatLossDelegate(this)

    open val annualHeatLoss: Double
        get() = heatLoss.sum()

    open val energyDemand by PipeEnergyDemandDelegate(this)

    val volumeFlow by PipeVolumeFlowDelegate(this)

    // Strömungsgeschwindigkeit = Volumenstrom / Rohrquerschnittsfläche
    val flowRate by PipeFlowRateDelegate(this)

    // Druckverluste in Bar. *2 für Hin und Rückleitung.
    val pipePressureLoss by PipePressureLossDelegate(this)

    val totalPressureLoss by PipeTotalPressureLossDelegate(this)

    val totalPumpPower by PipePumpPowerDelegate(this)

    open val investCost: Double
        get() = type.costPerMeter * length

    init {
        if (id.isBlank())
            throw IllegalArgumentException("Id of pipe must be filled.")

        if (source == target)
            throw IllegalArgumentException("Source and target node of pipe $id can not be the same.")

        if (length <= 0.0)
            throw IllegalArgumentException("Length of pipe $id can not be negative or zero.")

        if (coverageHeight < 0.0)
            throw IllegalArgumentException("Coverage height of pipe $id can not be negative.")
    }

}
