package de.fhac.ewi.model

class OutputNode(
    id: String,
    val thermalEnergyDemand: Double, // kwh per year
    val pressureLoss: Double // Bar
) : Node(id) {

    init {
        if (thermalEnergyDemand <= 0)
            throw IllegalArgumentException("Thermal energy demand can not be negative or zero.")

        if (pressureLoss !in 0.5..1.0)
            throw IllegalArgumentException("Pressure loss must be between 0.5 and 1 Bar.")
    }

    override val connectedPressureLoss: Double
        get() = super.connectedPressureLoss + pressureLoss

    override val connectedThermalEnergyDemand: Double
        get() = super.connectedThermalEnergyDemand + thermalEnergyDemand

    override fun connectChild(pipe: Pipe) =
        throw IllegalArgumentException("Output can't have child nodes. (invalid $pipe)")
}