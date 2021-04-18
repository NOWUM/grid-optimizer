import {Popover} from "@material-ui/core";
import React from "react";
import {AddCircleOutline, Edit} from "@material-ui/icons";
import {showSplitEdgeDialog} from "./EdgeContextOverlay";

export const EdgePopover = ({target, onSplitEdge}: {target: (Element | null), onSplitEdge: () => void}) => {
    return <Popover
        id={"dawe"}
        open={!!target}
        anchorEl={target}
        anchorOrigin={{
            vertical: 'top',
            horizontal: 'left',
        }}
        transformOrigin={{
            vertical: 'top',
            horizontal: 'left',
        }}
    >
        <AddCircleOutline onClick={() => showSplitEdgeDialog("", () => onSplitEdge, () => console.log("Nothing to do here")) }/>
        <Edit />
    </Popover>
}
