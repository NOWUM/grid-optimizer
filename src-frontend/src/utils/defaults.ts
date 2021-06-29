import {MassenstromResponse, NodeElements, OptimizationMetadata} from "../models";
import {GridSpacing} from "@material-ui/core";

export const defaultOptimizationMetadata: OptimizationMetadata = {
    pipeTypes: [
        {diameter: 20, costPerMeter: 432.37, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 25, costPerMeter: 437.85, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 32, costPerMeter: 467.11, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 40, costPerMeter: 483.56, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 50, costPerMeter: 547.55, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 65, costPerMeter: 594.17, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 80, costPerMeter: 681.0, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 100, costPerMeter: 840.97, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 125, costPerMeter: 1010.08, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 150, costPerMeter: 1217.58, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 200, costPerMeter: 1449.76, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 250, costPerMeter: 1941.55, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 300, costPerMeter: 2433.33, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 300, costPerMeter: 2925.12, isolationThickness: 40, distanceBetweenPipes: 200},
        {diameter: 300, costPerMeter: 3416.9, isolationThickness: 40, distanceBetweenPipes: 200},
    ],
    gridOperatingCostTemplate: "x*0.001", // f(gridInvestCost) = y [€/year]
    pumpInvestCostTemplate: "500+x*500", // f(Leistung) = y [€/kW]
    heatGenerationCost: 0.3, // €/kWh [for calculating heat loss]
    lifespanOfGrid: 25.0, // Jahre
    lifespanOfPump: 10.0,
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
