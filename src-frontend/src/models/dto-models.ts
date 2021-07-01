import {Costs, HotWaterGrid, OptimizedPipe, PipeType} from "./models";
import {List} from "@material-ui/core";

export interface OptimizationMetadata {
    pipeTypes: PipeType[],
    gridOperatingCostTemplate: String, // f(gridInvestCost) = y [€/year]
    pumpInvestCostTemplate: String, // f(Leistung) = y [€/kW]
    heatGenerationCost: number, // €/kWh [for calculating heat loss]
    lifespanOfGrid: number, // Jahre
    lifespanOfPump: number, // Jahre
    wacc: number, // Weighted Average Cost of Capital in %
    electricityCost: number, // ct/kWh [for pump station]
    electricalEfficiency: number, // for pump
    hydraulicEfficiency: number, // for pump
}


export interface OptimizationRequest extends OptimizationMetadata {
    grid: HotWaterGrid;
}

export interface PipeOptimization {
    diameter?: number;
    isCritical?: boolean;
    isLongest?: boolean;

    volumeFlow?: number[],
    pipeHeatLoss?: number[],
    pipePressureLoss?: number[],
    totalPressureLoss?: number[],
    totalPumpPower?: number[]
}


export interface OptimizationResult {
    costs: Costs,
    optimizedPipes: OptimizedPipe[],
    optimizedNodes: OptimizedNode[],
    criticalPath: string[],
}

export interface OptimizationStatusResponse {
    id: string,
    completed: boolean,
    numberOfChecks: number,
    numberOfUpdates: number,
    totalEnergyDemand: number,
    totalHeatLoss: number,
    neededPumpPower: number,
    totalPressureLoss: number,
    pressureLossCritical: number,
    pressureLossLongest: number,
    massenstromInput: number
}

export interface OptimizationOverviewResponse {
    costs: Costs,
    criticalPath: string[],
    longestPath: string[],
    optimizedPipes: OptimizedPipeTypeResponse[]
}

export interface OptimizedPipeTypeResponse {
    pipeId: string,
    diameter: number
}


export interface OptimizedNode {
    nodeId: string,
    thermalEnergyDemand: number[],
    connectedPressureLoss: number[],
    neededPumpPower: number[],
    flowInTemperature: number[],
    flowOutTemperature: number[],

    annualEnergyDemand: number,
    maximalNeededPumpPower: number,
    maximalPressureLoss: number
}
