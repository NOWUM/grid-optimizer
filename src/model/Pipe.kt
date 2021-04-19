package de.fhac.ewi.model

data class Pipe(
    val id: String,
    val source: Node,
    val target: Node,
    val length: Double
)
