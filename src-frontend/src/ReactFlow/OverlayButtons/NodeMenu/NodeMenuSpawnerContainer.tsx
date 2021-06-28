import {BaseNode} from "../../../models";
import React from "react";
import {InputNodeSpawner} from "./InputNodeSpawner";
import {IntermediateNodeSpawner} from "./IntermediateNodeSpawner";
import {OutputNodeSpawner} from "./OutputNodeSpawner";
import "./node-menu-spawner.css"
import IdGenerator from "../../../utils/IdGenerator";

export interface NodeSpawner {
    onNewNode: (newNode: BaseNode) => void
}

export const NodeMenuSpawnerContainer = ({onNewNode}: NodeSpawner) => {

    const handleNewNode = (baseNode: BaseNode ) =>{
        baseNode.id = IdGenerator.getNextNodeId(baseNode.data.label);
        onNewNode(baseNode)
    }

    return <div className={"node-menu-container"}>
        <InputNodeSpawner onNewNode={handleNewNode}/>
        <IntermediateNodeSpawner onNewNode={handleNewNode}/>
        <OutputNodeSpawner onNewNode={handleNewNode}/>
    </div>
}
