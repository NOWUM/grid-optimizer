import React from "react";
import {NodeSpawner} from "./NodeMenuSpawnerContainer";
import {Button} from "@material-ui/core";
import {IntermediateNode, NodeType} from "../models";
import {generateUniqueID} from "web-vitals/dist/modules/lib/generateUniqueID";
import {showNodeIntermediateDialog} from "../Overlays/NodeContextOverlay";


export const IntermediateNodeSpawner = ({onNewNode}: NodeSpawner) => {
    const defaultNode: IntermediateNode = {
        connect_limit: 3,
        data: {label: "Default Kreuzungspunkt"},
        position: {x: 300, y: 300},
        type: NodeType.INTERMEDIATE_NODE,
        id: generateUniqueID()
    }


    const handleClick = () => {
        showNodeIntermediateDialog("Erzeuge einen neuen Kreuzungspunkt", defaultNode, onNewNode, () => {/*Nothing to do here*/})
    }

    return <Button onClick={handleClick}>
        Intermediate
    </Button>
}
