package de.fhac.ewi.model

open class OutputNode(
    id: String,
    val thermalEnergyDemand: HeatDemandCurve, // Wh per hour in year year
    val staticPressureLoss: Double // Bar
) : Node(id) {

    init {
        if (thermalEnergyDemand.total <= 0)
            throw IllegalArgumentException("Thermal energy demand can not be negative or zero.")

        if (staticPressureLoss !in 0.5..1.0)
            throw IllegalArgumentException("Pressure loss must be between 0.5 and 1 Bar.")
    }

    override val pressureLoss: DoubleArray = DoubleArray(8760) { staticPressureLoss }

    override val energyDemand = thermalEnergyDemand.curve.toDoubleArray()

    open val annualEnergyDemand: Double
        get() = thermalEnergyDemand.curve.sum()

    override fun connectChild(pipe: Pipe) =
        throw IllegalArgumentException("Output node $id can't have child nodes. Pipe ${pipe.id} invalid.")
}