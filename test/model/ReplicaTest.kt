package de.fhac.ewi.model

import de.fhac.ewi.services.HeatDemandService
import de.fhac.ewi.services.LoadProfileService
import de.fhac.ewi.services.TemperatureTimeSeriesService
import de.fhac.ewi.util.loadHProfiles
import de.fhac.ewi.util.loadTemperatureTimeSeries
import de.fhac.ewi.util.round
import de.fhac.ewi.util.toDoubleFunction
import kotlin.test.Test
import kotlin.test.assertEquals

class ReplicaTest {

    private val timeSeriesService = TemperatureTimeSeriesService(loadTemperatureTimeSeries())
    private val loadProfileService = LoadProfileService(loadHProfiles())
    private val heatDemandService = HeatDemandService(timeSeriesService, loadProfileService)

    @Test
    fun compareNonReplicaToReplica() {
        val pipeType = PipeType(0.02, 10.0, 0.5, 0.5)
        val heat = heatDemandService.createCurve(20_000.0, "EFH", "Schemm 2018")

        val replicas = 10

        val nonReplica = createBaseGrid()
        repeat(replicas) {
            nonReplica.addOutputNode("Out$it", heat, 0.6)
            nonReplica.addPipe("Pipe$it", "2", "Out$it", 10.0, 0.6)
        }
        nonReplica.pipes.forEach { it.type = pipeType }

        val replica = createBaseGrid()
        replica.addOutputNode("Out", heat, 0.6, replicas)
        replica.addPipe("Pipe", "2", "Out", 10.0, 0.6)
        replica.pipes.forEach { it.type = pipeType }

        assertEquals(nonReplica.pipes.sumOf { it.investCost }, replica.pipes.sumOf { it.investCost }, "Pipe invest costs")
        assertEquals(nonReplica.input.pressureLoss.maxOrNull(), replica.input.pressureLoss.maxOrNull(), "Max pressure loss")
        assertEquals(nonReplica.input.volumeFlow.sum(), replica.input.volumeFlow.sum(), "Max pressure loss")
        assertEquals(nonReplica.neededPumpPower, replica.neededPumpPower, "Needed pump power")
        assertEquals(nonReplica.totalOutputEnergy, replica.totalOutputEnergy, "Total output energy")
        assertEquals(nonReplica.totalHeatLoss.round(5), replica.totalHeatLoss.round(5), "Total heat loss")
    }

    private fun createBaseGrid(): Grid {
        val grid = Grid()
        grid.addInputNode(
            "1",
            timeSeriesService.getSeries("Schemm 2018"),
            "70".toDoubleFunction(),
            "65".toDoubleFunction()
        )
        grid.addIntermediateNode("2")
        grid.addPipe("P1", "1", "2", 100.0, 0.6)
        return grid
    }
}