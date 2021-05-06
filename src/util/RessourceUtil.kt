package de.fhac.ewi.util

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import de.fhac.ewi.model.HeatDemandCurve
import de.fhac.ewi.model.TemperatureTimeSeries
import de.fhac.ewi.model.heatprofile.HProfile
import de.fhac.ewi.model.heatprofile.HourDistribution
import java.io.BufferedReader
import java.io.InputStreamReader

private fun getResourceAsStream(resource: String) =
    TemperatureTimeSeries::class.java.classLoader.getResourceAsStream(resource)

fun getResourceFiles(path: String): List<String> = getResourceAsStream(path).use{
    return if (it == null) emptyList() else BufferedReader(InputStreamReader(it)).readLines()
}

fun loadTemperatureTimeSeries(): List<TemperatureTimeSeries> {
    return csvReader { delimiter = ';' }
        .readAll(getResourceAsStream("temperature-time-series.csv")!!)
        .transpose()
        .map { TemperatureTimeSeries(it.first(), it.drop(1).map(String::toDouble)) }
}

fun loadHProfiles(): List<HProfile> {
    val reader = csvReader { delimiter = ';' }
    return getResourceFiles("/loadprofiles/").map { profile ->
        val ressource = getResourceAsStream("/loadprofiles/$profile")!!
        val input = reader.readAll(ressource)
        val parameters = input.first().map { it.toDouble() }
        val hourDistribution = input.drop(1).map { it.map { it.toDouble() } }
            .associate { list -> list.component1()..list.component2() to list.drop(2) }
            .run { HourDistribution(this) }
        HProfile(
            profile.dropLast(4), parameters[0], parameters[1], parameters[2],
            parameters[3], parameters[4], parameters[5], parameters[6], parameters[7], parameters[8],
            hourDistribution
        )
    }
}