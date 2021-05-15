import {Edge, Node} from 'react-flow-renderer';

export interface NodeElements {
    inputNodes: InputNode[],
    intermediateNodes: IntermediateNode[],
    outputNodes: OutputNode[],
}
export interface HotWaterGrid extends NodeElements{
    pipes: Pipe[],
    temperatureSeries: string
}

export interface BaseNode extends Node{
    id: string,
    type: NodeType
}

export interface InputNode extends BaseNode{
    flowTemperatureTemplate: string, // mathematical expression like `x+5` with x as outside temperature
    returnTemperatureTemplate: string // mathematical expression like `x+5` with x as outside temperature
}

export interface IntermediateNode extends BaseNode{
    connect_limit: 3
}

export interface OutputNode extends BaseNode {
    thermalEnergyDemand: number, // kwh per year
    pressureLoss: number, // Bar,
    loadProfileName: string
}

export enum LoadProfile{
    SLP
}

export interface Pipe extends Edge{
    length: number
}

export enum NodeType {
    INPUT_NODE="INPUT_NODE",
    INTERMEDIATE_NODE="INTERMEDIATE_NODE",
    OUTPUT_NODE="OUTPUT_NODE"
}


