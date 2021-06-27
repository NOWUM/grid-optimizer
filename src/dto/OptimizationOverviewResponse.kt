package de.fhac.ewi.dto

import de.fhac.ewi.model.Costs

data class OptimizationOverviewResponse(
    val costs: Costs,
    val criticalPath: List<String>,
    val optimizedPipes: List<OptimizedPipeTypeResponse>
)

