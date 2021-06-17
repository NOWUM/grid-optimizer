import {Popover, Tooltip} from "@material-ui/core";
import React from "react";
import {AddCircleOutline, DeleteForeverOutlined, Edit} from "@material-ui/icons";
import {showEditPipeDialog,} from "./EdgeContextOverlay";
import {Pipe} from "../../models";

export const EdgePopover = ({target, onSplitEdge, onEditEdge, pipe, onRemoveEdge}: {
                                target: (Element | null),
                                onSplitEdge: (id: string, length1: number, length2: number) => void,
                                onEditEdge: (id: string, length: number, coverageHeight: number) => void, pipe: Pipe
                                onRemoveEdge: () => void
                            },
) => {
    return <Tooltip title={"Das ist ein Test"}><Popover
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
        <Edit
            onClick={() => showEditPipeDialog("Teile einen Leitungsabschnitt auf",
                (id: string, length: number, coverageHeight) => onEditEdge(id, length, coverageHeight),
                () => console.log("Nothing to do here"),
                pipe?.id, pipe?.length, pipe?.coverageHeight)}
            id={pipe?.id}
        />
        <DeleteForeverOutlined onClick={onRemoveEdge}/>
    </Popover></Tooltip>
}
