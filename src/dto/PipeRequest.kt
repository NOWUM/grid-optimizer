package de.fhac.ewi.dto

data class PipeRequest(
    val source: String,
    val target: String,

    // Length of pipe
    val length: Double
) {
    override fun toString() = "Pipe [$source] -> [$target]"
}
