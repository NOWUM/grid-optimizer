import React from "react";
import {Button} from "@material-ui/core";
import {NodeType, OutputNode} from "../models";
import {generateUniqueID} from "web-vitals/dist/modules/lib/generateUniqueID";
import {showNodeOutputDialog} from "../Overlays/NodeContextOverlay";
import {NodeSpawner} from "./NodeMenuSpawnerContainer";


export const OutputNodeSpawner = ({onNewNode}: NodeSpawner) => {
    const defaultNode: OutputNode = {
        loadProfileName: "",
        thermalEnergyDemand: 123,
        pressureLoss: 0.56,
        data: {label: "Default Node"},
        position: {x: 300, y: 300},
        type: NodeType.OUTPUT_NODE,
        id: generateUniqueID()
    }


    const handleClick = () => {
        showNodeOutputDialog("Erzeuge eine neue Output Node", defaultNode,
            (node) => {
                console.log(node)
                onNewNode(node)
            },() => {/*Nothing to do here*/})
    }

    return <Button onClick={handleClick}>
        Output
    </Button>
}
