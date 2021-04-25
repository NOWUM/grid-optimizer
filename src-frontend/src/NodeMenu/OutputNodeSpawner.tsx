import React from "react";
import {NodeSpawner} from "./NodeMenuSpawnerContainer";
import {Button} from "@material-ui/core";
import {LoadProfile, NodeType, OutputNode} from "../models";
import {generateUniqueID} from "web-vitals/dist/modules/lib/generateUniqueID";


export const OutputNodeSpawner = ({onNewNode}: NodeSpawner) => {
    const defaultNode: OutputNode = {
        area: 123,
        hotWater: 456,
        loadProfile: LoadProfile.SLP,
        data: {label: "Default Node"},
        position: {x: 300, y: 300},
        type: NodeType.OUTPUT_NODE,
        id: generateUniqueID()
    }


    const handleClick = () => {
        onNewNode(defaultNode)
    }

    return <Button onClick={handleClick}>
        Output
    </Button>
}
