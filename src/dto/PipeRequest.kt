package de.fhac.ewi.dto

data class PipeRequest(
    val id: String,
    val source: String,
    val target: String,

    val length: Double, // in m
    val coverageHeight: Double // in m
) {
    override fun toString() = "Pipe#$id [$source] -> [$target]"
}
