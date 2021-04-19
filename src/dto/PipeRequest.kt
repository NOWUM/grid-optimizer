package de.fhac.ewi.dto

data class PipeRequest(
    val id: String,
    val source: String,
    val target: String,

    // Length of pipe
    val length: Double
) {
    override fun toString() = "Pipe#$id [$source] -> [$target]"
}
