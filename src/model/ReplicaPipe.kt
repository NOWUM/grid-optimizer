package de.fhac.ewi.model

import de.fhac.ewi.model.math.ReplicaPipeEnergyDemandDelegate

class ReplicaPipe(
    id: String,
    source: Node,
    private val replicaTarget: ReplicaOutputNode,
    length: Double,
    coverageHeight: Double
) : Pipe(id, source, replicaTarget, length, coverageHeight) {

    override val energyDemand: DoubleArray by ReplicaPipeEnergyDemandDelegate(this)

    override val annualHeatLoss: Double
        get() = super.annualHeatLoss * replicaTarget.replicas

    override val investCost: Double
        get() = super.investCost * replicaTarget.replicas
}