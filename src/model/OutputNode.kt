package de.fhac.ewi.model

class OutputNode(
    id: String,
    val hotWater: Double,
    val area: Double,
    val loadProfile: LoadProfile
) : Node(id) {

    val usage: Double by lazy { hotWater + area * loadProfile.ordinal }

    override fun connectChild(pipe: Pipe) =
        throw IllegalArgumentException("Output can't have child nodes. (invalid $pipe)")
}