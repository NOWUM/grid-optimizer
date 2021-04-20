import {Popover} from "@material-ui/core";
import React from "react";
import {AddCircleOutline, DeleteForeverOutlined, Edit} from "@material-ui/icons";
import {showEditPipeDialog, showSplitPipeDialog,} from "./EdgeContextOverlay";

export const EdgePopover = ({target, onSplitEdge, onEditEdge, targetId, onRemoveEdge}: {
                                target: (Element | null),
                                onSplitEdge: (id: string, length1: number, length2: number) => void,
                                onEditEdge: (id: string, length: number) => void, targetId: string
                                onRemoveEdge: () => void
                            },
) => {
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
        <AddCircleOutline
            onClick={() => {
                console.log(target)
                showSplitPipeDialog("Editiere eine Leitung.",
                    (id: string, length1: number, length2: number) => onSplitEdge(id, length1, length2),
                    () => console.log("Nothing to do here"),
                    targetId)
            }}
        />
        <Edit
            onClick={() => showEditPipeDialog("Teile einen Leitungsabschnitt auf",
                (id: string, length: number) => onEditEdge(id, length),
                () => console.log("Nothing to do here"),
                targetId)}
            id={targetId}
        />
        <DeleteForeverOutlined onClick={onRemoveEdge}/>
    </Popover>
}
