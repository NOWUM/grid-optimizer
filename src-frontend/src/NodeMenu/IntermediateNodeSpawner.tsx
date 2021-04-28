import React from "react";
import {NodeSpawner} from "./NodeMenuSpawnerContainer";
import {Button} from "@material-ui/core";
import {IntermediateNode, NodeType} from "../models";
import {generateUniqueID} from "web-vitals/dist/modules/lib/generateUniqueID";
import {showNodeIntermediateDialog} from "../Overlays/NodeContextOverlay";


export const IntermediateNodeSpawner = ({onNewNode}: NodeSpawner) => {
    const defaultNode: IntermediateNode = {
        connect_limit: 3,
        data: {label: "Default Node"},
        position: {x: 300, y: 300},
        type: NodeType.INTERMEDIATE_NODE,
        id: generateUniqueID()
    }


    const handleClick = () => {
        showNodeIntermediateDialog("Erzeuge eine neue Intermediate Node", defaultNode, onNewNode, () => {})
    }

    return <Button onClick={handleClick}>
        Intermediate
    </Button>
}
