import {MassenstromResponse, NodeElements, OptimizationMetadata} from "../models";

export const defaultOptimizationMetadata: OptimizationMetadata = {
    insulationThickness: 0.0,
    gridInvestCostTemplate: "", // f(Durchmesser) = y [€/m]
    gridOperatingCostTemplate: "", // f(gridInvestCost) = y [€/year]
    pumpInvestCostTemplate: "", // f(Leistung) = y [€/kW]
    heatGenerationCost: 0.0, // €/kWh [for calculating heat loss]
    lifespanOfResources: 0.0, // Jahre
    wacc: 0.0, // Weighted Average Cost of Capital in %
    electricityCost: 0.0, // ct/kWh [for pump station]
    electricalEfficiency: 0.0, // for pump
    hydraulicEfficiency: 0.0, // for pump
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


export const DEFAULT_GRID_SPACING = 3
