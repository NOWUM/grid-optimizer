import {BaseNode} from "../../../models";
import React from "react";
import {InputNodeSpawner} from "./InputNodeSpawner";
import {IntermediateNodeSpawner} from "./IntermediateNodeSpawner";
import {OutputNodeSpawner} from "./OutputNodeSpawner";
import "./node-menu-spawner.css"

export interface NodeSpawner {
    onNewNode: (newNode: BaseNode) => void
}

export const NodeMenuSpawnerContainer = ({onNewNode}: NodeSpawner) => {
    return <div className={"node-menu-container"}>
        <InputNodeSpawner onNewNode={(baseNode: BaseNode) => onNewNode(baseNode)}/>
        <IntermediateNodeSpawner onNewNode={(baseNode: BaseNode) => onNewNode(baseNode)}/>
        <OutputNodeSpawner onNewNode={(baseNode: BaseNode) => {
            console.log(baseNode)
            onNewNode(baseNode)}}/>
    </div>
}
