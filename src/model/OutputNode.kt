package de.fhac.ewi.model

class OutputNode(
    id: String,
    val hotWater: Double,
    val area: Double,
    val loadProfile: LoadProfile
) : Node(id) {
    val usage = hotWater + area * loadProfile.ordinal // TODO ?
}