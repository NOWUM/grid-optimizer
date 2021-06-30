package de.fhac.ewi.util

import de.fhac.ewi.model.*
import excelkt.Sheet
import excelkt.Workbook
import excelkt.workbook
import java.io.File

fun createExcelFile(filename: String, optimizer: Optimizer): File {
    val file = File(filename)
    file.createOrEmptyFile()
    workbook {

        // Allgemein
        addGridLayoutSheet(optimizer.grid)

        // Übersicht
        addDataSheet(
            "Knoten",
            listOf("Typ", "ID", "Energiebedarf [MW/a]", "Energiebedarf Peak [MW]", "Max Massenstrom [kg/s]"),
            optimizer.grid.nodes.map {
                listOf(
                    it.javaClass.simpleName,
                    it.id,
                    it.energyDemand.sum().toMW(),
                    it.energyDemand.maxOrElse().toMW(),
                    it.massenstrom.maxOrElse()
                )
            },
            transpose = false
        )

        addDataSheet(
            "Leitungen",
            listOf(
                "ID",
                "Länge [m]",
                "Durchmesser [mm]",
                "Max Druckverlust [Bar]",
                "Wärmeverlust [kWh/a]",
                "Kosten [€]"
            ),
            optimizer.grid.pipes.map {
                listOf(
                    it.id,
                    it.length,
                    it.type.diameter,
                    it.pipePressureLoss.maxOrElse(),
                    it.annualHeatLoss.toKW(),
                    it.investCost
                )
            },
            transpose = false
        )

        addGridCostSheet(optimizer.gridCosts, optimizer.investParams)
       /* addDataSheet("Wärmebedarf Knoten",
            nodes.map { it.id },
            nodes.map { it.energyDemand.toList() })

         addDataSheet("Massenstrom Knoten",
            nodes.map { it.id },
            nodes.map { it.massenstrom.toList() }) */

    }.write(file.outputStream())
    return file
}

private fun Workbook.addGridCostSheet(gridCosts: Costs, investParams: InvestmentParameter) {
    sheet("Kosten") {
        addTitle("Kosten")

        row {
            cell("")
            cell("Gesamt")
            cell("Lebensdauer")
            cell("Annuitätsfaktor")
            cell("Kosten/a")
        }
        row {            cell("Investitionen")        }
        row {
            cell("Leitungen")
            cell(gridCosts.pipeInvestCostTotal.round(2))
            cell(investParams.lifespanOfGrid)
            cell(investParams.pipeAnnuityFactor)
            cell(gridCosts.pipeInvestCostAnnuity.round(2))
        }
        row {
            cell("Pumpe")
            cell(gridCosts.pumpInvestCostTotal.round(2))
            cell(investParams.lifespanOfPump)
            cell(investParams.pumpAnnuityFactor)
            cell(gridCosts.pumpInvestCostAnnuity.round(2))
        }
        row { cell("Betriebskosten") }
        row {
            cell("Leitungen")
            repeat(3) { cell("") }
            cell(gridCosts.pipeOperationCost.round(2))
        }
        row {
            cell("Pumpe")
            repeat(3) { cell("") }
            cell(gridCosts.pumpOperationCost.round(2))
        }
        row {
            cell("Wärmeverlust")
            repeat(3) { cell("") }
            cell(gridCosts.heatLossCost.round(2))
        }
        row {  }
        row {
            repeat(3) {cell("")}
            cell("Summe")
            cell(gridCosts.totalPerYear.round(2))
        }
    }

}

/**
 * Erstellt ein Sheet mit dem Trassenplan.
 *
 * @receiver Workbook
 * @param grid Grid
 */
fun Workbook.addGridLayoutSheet(grid: Grid) {
    sheet("Trassenplan") {
        addTitle("Trassenplan")

        val maxDepth = grid.mostDistantNode.pathToSource.size + 3
        row {
            repeat(maxDepth) { cell("") }
            cell("Wärmebedarf [MWh/a]")
            cell("Druckverlust [Bar]")
        }
        row {
            cell("${grid.input.javaClass.simpleName} ${grid.input.id}")
        }
        row {
            cell("+ === Pumpe")
            cell(
                "${grid.neededPumpPower.toKW().round(3)} kW (für ${
                    grid.input.pressureLoss.maxOrElse().round(2)
                } Bar)"
            )
        }
        for (child in grid.input.connectedChildNodes) {
            drawNode(child, maxDepth)
        }
    }
}

fun Sheet.addTitle(title: String) {
    val headingStyle = createCellStyle {
        setFont(createFont {
            fontHeightInPoints = 20
        })
    }
    row(headingStyle) {
        cell(title)
    }
    repeat(2) { row { } }
}

fun <T : Any, U : Any> Workbook.addDataSheet(
    name: String,
    headings: List<U>,
    data: List<List<T>>,
    transpose: Boolean = true
) {
    sheet(name) {
        addTitle(name)
        writeHeadings(headings)

        (if (transpose) data.transpose() else data).forEach { rowData ->
            row {
                rowData.forEach { cellData ->
                    cell(cellData)
                }
            }
        }
    }
}

fun <U : Any> Sheet.writeHeadings(headings: List<U>) {
    val headingStyle = createCellStyle {
        setFont(createFont {
            bold = true
            fontHeightInPoints = 14
        })
    }

    row(headingStyle) {
        headings.forEach { cell(it) }
    }
}

private fun Sheet.drawNode(node: Node, maxDepth: Int, depth: Int = 0) {
    row {
        repeat(depth) {
            cell("")
        }
        cell(
            node.pathToSource.first()
                .run { "+ --- Leitung $id Ø ${(type.diameter * 1000).toInt()} mm (${length.round(1)} m)" })
        cell("${node.javaClass.simpleName} ${node.id}")

        if (node is OutputNode) {
            repeat(maxDepth - depth - 2) { cell("") }
            // Energiebedarf
            cell(node.annualEnergyDemand.toMW())
            // Druckverlust
            cell((node.staticPressureLoss + node.pathToSource.sumOf { it.pipePressureLoss.maxOrElse() }).round(3))

        }
    }
    for (child in node.connectedChildNodes) {
        drawNode(child, maxDepth, depth + 1)
    }
}
