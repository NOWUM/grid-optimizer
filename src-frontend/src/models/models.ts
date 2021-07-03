import {Edge, Node, Position} from 'react-flow-renderer';
import {PipeOptimization} from "./dto-models";

export interface NodeElements {
    inputNodes: InputNode[],
    intermediateNodes: IntermediateNode[],
    outputNodes: OutputNode[],
}
export interface HotWaterGrid extends NodeElements{
    pipes: Pipe[],
    temperatureSeries: string
}

export interface BaseNode extends Node, NodeOptimization{
    id: string,
    type: NodeType,
}

export interface NodeOptimization {
    optimizedThermalEnergyDemand?: number[],
    massenstrom?: number[],
    connectedPressureLoss?: number[],
    neededPumpPower?: number[],
    flowInTemperature?: number[],
    flowOutTemperature?: number[],

    annualEnergyDemand?: number,
    maximalNeededPumpPower?: number,
    maximalPressureLoss?: number
}

export interface InputNode extends BaseNode{
    flowTemperatureTemplate: string, // mathematical expression like `x+5` with x as outside temperature
    returnTemperatureTemplate: string // mathematical expression like `x+5` with x as outside temperature
}


export interface IntermediateNode extends BaseNode{
    connect_limit: 3
}

export interface OutputNode extends BaseNode{
    thermalEnergyDemand: number, // kwh per year
    pressureLoss: number, // Bar,
    loadProfileName: string,
    replicas?: number
}

export enum LoadProfile{
    SLP
}

export interface Pipe extends Edge, PipeOptimization{
    length: number,
    coverageHeight: number
}

export enum NodeType {
    INPUT_NODE = "INPUT_NODE",
    INTERMEDIATE_NODE = "INTERMEDIATE_NODE",
    OUTPUT_NODE = "OUTPUT_NODE"
}

export interface OptimizedPipe {
    pipeId: string,
    diameter: number
}


export interface HeatDemand {
    temperatureSeries: string,
    loadProfileName: string,
    thermalEnergyDemand: number // kwh per year
}
export interface HeatDemandResult {
    curve: number[],
    temperature: number[],
    allokation: number[],
    dailyHeatCurve: number[]
}

export interface Costs {
    pipeInvestCostTotal: number, // Investitionskosten Netz
    pipeInvestCostAnnuity: number, // Investitionskosten Netz pro Jahr
    pipeOperationCost: number, // Betriebskosten Netz per year
    pumpInvestCostTotal: number, // Investitionskosten Pumpe
    pumpInvestCostAnnuity: number, // Investitionskosten Pumpe pro Jahr
    pumpOperationCost: number, // Betriebskosten Pumpe per year
    heatLossCost: number, // Wärmeverlustkosten per year
    totalPerYear: number // Gesamtkosten
}

export interface PipeType {
    diameter: number, // in m
    costPerMeter: number, // in €
    isolationThickness: number, // in mm
    distanceBetweenPipes: number // in mm
}

export enum TabEnum {
    FORMULA_CHECK= "0",
    META_DATA = "1",
    EDITOR = "2",
    OPTIMIZATION = "3"
}

export const instanceOfHotWaterGrid = (object: any): object is HotWaterGrid => {
    if (!object) {
        return false
    }
    const pipes = object.pipes && object.pipes.map((p: any) => instanceOfPipe(p)).every((b: boolean) => b)
    const inputNodes = object.inputNodes && object.inputNodes.map((p: any) => instanceOfInputNode(p)).every((b: boolean) => b)
    const intermediateNodes = object.intermediateNodes && object.intermediateNodes.map((p: any) => instanceOfIntermediateNode(p)).every((b: boolean) => b)
    const outputNodes = object.outputNodes && object.outputNodes.map((p: any) => instanceOfOutputNode(p)).every((b: boolean) => b)
    return pipes && inputNodes && intermediateNodes && outputNodes;
}

export const instanceOfPipe = (pipe: any): pipe is Pipe => {
    if (!pipe) {
        return false
    }
    const length = instanceOfNumber(pipe.length)
    const coverageHeight = instanceOfNumber(pipe.coverageHeight)
    const source = instanceOfString(pipe.source)
    const target = instanceOfString(pipe.target)
    const id = instanceOfString(pipe.id)
    return length && coverageHeight && source && target && id;
}

export const instanceOfPosition = (position: any): position is Position => {
    if (!position) {
        return false
    }
    const isCoordinate = (c: any) => c && typeof(c) === "number"
    return isCoordinate(position.x) && isCoordinate(position.y)
}

export const instanceOfBaseNode = (baseNode: any): baseNode is BaseNode => {
    if (!baseNode) {
        return false
    }
    const id = instanceOfString(baseNode.id)
    const position = baseNode.position && instanceOfPosition(baseNode.position)
    const label = baseNode.data && instanceOfString(baseNode.data.label)
    const type = instanceOfString(baseNode.type)
    return id && position && label && type
}

export const instanceOfInputNode = (inputNode: any): inputNode is InputNode => {
    if(!inputNode) {
        return false;
    }
    const flowTemperatureTemplate = instanceOfString(inputNode.flowTemperatureTemplate)
    const returnTemperatureTemplate = instanceOfString(inputNode.returnTemperatureTemplate)

    return instanceOfBaseNode(inputNode) && flowTemperatureTemplate && returnTemperatureTemplate
}

export const instanceOfIntermediateNode = (intermediateNode: any): intermediateNode is IntermediateNode => {
    if(!intermediateNode) {
        return false;
    }
    return instanceOfBaseNode(intermediateNode);
}

export const instanceOfOutputNode = (outputNode: any): outputNode is OutputNode => {
    if(!outputNode) {
        return false;
    }

    const thermalEnergyDemand = instanceOfNumber(outputNode.thermalEnergyDemand)
    const pressureLoss = instanceOfNumber(outputNode.pressureLoss)
    const loadProfileName = instanceOfString(outputNode.loadProfileName)

    return instanceOfBaseNode(outputNode) && thermalEnergyDemand && pressureLoss && loadProfileName;
}

const instanceOfString = (o: any): o is string => o && typeof(o) === "string"
const instanceOfNumber = (o: any): o is number => o && typeof(o) === "number"
