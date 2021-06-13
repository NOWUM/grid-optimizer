import {MassenstromResponse, NodeElements, OptimizationMetadata} from "../models";
import {GridSpacing} from "@material-ui/core";

export const defaultOptimizationMetadata: OptimizationMetadata = {
    pipeTypes: [{diameter: 1, costPerMeter: 2}, {diameter: 2, costPerMeter: 3},
        {diameter: 3, costPerMeter: 4},
        {diameter: 4, costPerMeter: 5},
        {diameter: 5, costPerMeter: 6},
        {diameter: 6, costPerMeter: 7},
        {diameter: 7, costPerMeter: 8},],
    insulationThickness: 1,
    gridOperatingCostTemplate: "3+x", // f(gridInvestCost) = y [€/year]
    pumpInvestCostTemplate: "5-x", // f(Leistung) = y [€/kW]
    heatGenerationCost: 0.1, // €/kWh [for calculating heat loss]
    lifespanOfGrid: 25.0, // Jahre
    lifespanOfPump: 25.0,
    yearsOfOperation: 30.0,
    wacc: 12.0, // Weighted Average Cost of Capital in %
    electricityCost: 32.0, // ct/kWh [for pump station]
    electricalEfficiency: 0.12, // for pump
    hydraulicEfficiency: 0.31, // for pump
}

export const defaultMassenstrom: MassenstromResponse = {
    temperatures: [],
    flowInTemperatures: [],
    flowOutTemperatures: [],
    energyHeatDemand: [],
    massenstrom: []
}

export const defaultNodeElements: NodeElements = {
    inputNodes: [],
    intermediateNodes: [],
    outputNodes: []
}

export const defaultTemperatureKey: string = ""


export const DEFAULT_GRID_SPACING: GridSpacing = 3
