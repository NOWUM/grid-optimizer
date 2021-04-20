import ReactFlow, {
    Edge,
    Node
} from 'react-flow-renderer';


export interface HotWaterGrid {
    nodes: BaseNode[],
    pipes: Pipe[]
}

export interface BaseNode extends Node{
    id: string,
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
