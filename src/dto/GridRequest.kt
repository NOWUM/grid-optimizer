package de.fhac.ewi.dto

data class GridRequest(
    val inputNodes: List<InputNodeRequest>,
    val intermediateNodes: List<IntermediateNodeRequest>,
    val outputNodes: List<OutputNodeRequest>,
    val pipes: List<PipeRequest>
)
