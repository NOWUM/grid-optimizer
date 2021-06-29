package de.fhac.ewi.model

class ReplicaOutputNode(
    id: String,
    thermalEnergyDemand: HeatDemandCurve,
    staticPressureLoss: Double,
    val replicas: Int
) : OutputNode(id, thermalEnergyDemand, staticPressureLoss) {

    init {
        if (replicas < 2)
            throw IllegalArgumentException("Replica number for OutputNode must at least be set to two.")
    }

    override val annualEnergyDemand: Double
        get() = super.annualEnergyDemand * replicas
}