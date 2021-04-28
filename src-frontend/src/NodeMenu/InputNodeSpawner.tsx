import React from "react";
import {NodeSpawner} from "./NodeMenuSpawnerContainer";
import {Button} from "@material-ui/core";
import {InputNode, NodeType} from "../models";
import {generateUniqueID} from "web-vitals/dist/modules/lib/generateUniqueID";
import {showNodeInputDialog} from "../Overlays/NodeContextOverlay";


export const InputNodeSpawner = ({onNewNode}: NodeSpawner) => {
    const defaultNode: InputNode = {
        data: {label: "Default Node"},
        position: {x: 300, y: 300},
        id: generateUniqueID(),
        type: NodeType.INPUT_NODE,
        flowTemperatureTemplate: "x+3", // mathematical expression like `x+5` with x as outside temperature
        returnTemperatureTemplate: "x+2"
    }


    const handleClick = () => {
        showNodeInputDialog("Erzeuge eine neue Input Node", defaultNode, onNewNode, () => {} )
    }

    return <Button onClick={handleClick}>
        Input
    </Button>
}
