import ReactFlow, {
    Edge,
    Node
} from 'react-flow-renderer';

export interface NodeElements {
    inputNodes: InputNode[],
    intermediateNodes: IntermediateNode[],
    outputNodes: OutputNode[],
}
export interface HotWaterGrid extends NodeElements{
    pipes: Pipe[]
}

export interface BaseNode extends Node{
    id: string,
    type: NodeType
}

export interface InputNode extends BaseNode{}

export interface IntermediateNode extends BaseNode{
    connect_limit: 3
}

export interface OutputNode extends BaseNode{
    hotWater: number,
    area: number,
    loadProfile: LoadProfile
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
