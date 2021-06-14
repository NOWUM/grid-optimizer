import {MassenstromResponse, NodeElements, OptimizationMetadata} from "../models";
import {GridSpacing} from "@material-ui/core";

export const defaultOptimizationMetadata: OptimizationMetadata = {
    pipeTypes: [
        {diameter: 20, costPerMeter: 391.0},
        {diameter: 25, costPerMeter: 396.0},
        {diameter: 32, costPerMeter: 422.0},
        {diameter: 40, costPerMeter: 437.0},
        {diameter: 50, costPerMeter: 495.0},
        {diameter: 65, costPerMeter: 537.0},
        {diameter: 80, costPerMeter: 616.0},
        {diameter: 100, costPerMeter: 790.0},
        {diameter: 125, costPerMeter: 912.0},
        {diameter: 150, costPerMeter: 1101.0},
        {diameter: 200, costPerMeter: 1311.0},
        {diameter: 250, costPerMeter: 1755.0},
    ],
    gridOperatingCostTemplate: "x*0.001", // f(gridInvestCost) = y [€/year]
    pumpInvestCostTemplate: "300+x*0.05", // f(Leistung) = y [€/kW]
    heatGenerationCost: 0.3, // €/kWh [for calculating heat loss]
    lifespanOfGrid: 25.0, // Jahre
    lifespanOfPump: 10.0,
    yearsOfOperation: 20.0,
    wacc: 12.0, // Weighted Average Cost of Capital in %
    electricityCost: 0.3, // €/kWh [for pump station]
    electricalEfficiency: 0.9, // for pump
    hydraulicEfficiency: 0.75, // for pump
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

export const defaultTemperatureKey: string = "Schemm 2018"


export const DEFAULT_GRID_SPACING: GridSpacing = 3
