package de.fhac.ewi.model

data class Pipe(
    val id: String,
    val source: Node,
    val target: Node,
    val length: Double
) {

    var diameter: Double = 0.25

    init {
        if (id.isBlank())
            throw IllegalArgumentException("Id of pipe must be filled.")

        if (source == target)
            throw IllegalArgumentException("Source and target node of pipe can not be the same.")

        if (length <= 0.0)
            throw IllegalArgumentException("Length of pipe can not be negative or zero.")
    }
}
