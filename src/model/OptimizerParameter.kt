package de.fhac.ewi.model

data class OptimizerParameter(
    val skipIfGettingWorse: Boolean = true,
    val skipSmallerThenCurrent: Boolean = false,
    val skipBiggerThenCurrent: Boolean = false,
    val maxDifferenceToCurrent: Int = -1,
){
    fun range(pipe: Pipe, types: List<PipeType>): IntRange {
        // TODO
        return types.indices
    }
}
