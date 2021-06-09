package de.fhac.ewi.dto

import de.fhac.ewi.model.Costs

data class OptimizationResponse(
    val costs: Costs,
    val optimizedPipes: List<OptimizedPipeResponse>,
    val optimizedNodes: List<OptimizedNodeResponse>,
)
