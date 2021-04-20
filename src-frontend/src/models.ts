export interface HotWaterGrid {
    nodes: Node[],
    pipes: Pipe[]
}

export interface BaseNode{
    id: string
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

export interface Pipe{
    id: string,
    source: string,
    target: string,
    length: number
}
