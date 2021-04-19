package de.fhac.ewi.dto

import de.fhac.ewi.model.Node

data class GridRequest(
    val nodes: List<Node>,
    val pipes: List<PipeRequest>
)
