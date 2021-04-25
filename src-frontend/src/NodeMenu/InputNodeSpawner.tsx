import React from "react";
import {NodeSpawner} from "./NodeMenuSpawnerContainer";
import {Button} from "@material-ui/core";
import {InputNode, NodeType} from "../models";
import {generateUniqueID} from "web-vitals/dist/modules/lib/generateUniqueID";


export const InputNodeSpawner = ({onNewNode}: NodeSpawner) => {
    const defaultNode: InputNode = {
        data: {label: "Default Node"},
        position: {x: 300, y: 300},
        id: generateUniqueID(),
        type: NodeType.INPUT_NODE
    }


    const handleClick = () => {
        onNewNode(defaultNode)
    }

    return <Button onClick={handleClick}>
        Input
    </Button>
}
