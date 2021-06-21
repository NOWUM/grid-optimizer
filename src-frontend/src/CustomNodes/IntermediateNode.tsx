import {Handle, Position} from "react-flow-renderer";
import {customInputHandleStyle, customOutputHandleStyle, getOptimizationTooltip} from "./InputNode";
import {showNodeIntermediateDialog} from "../ReactFlow/Overlays/NodeContextOverlay";
import {IntermediateNode as IntermediateNodeModel} from "../models";
import {Tooltip} from "@material-ui/core";
import React from "react";


const customNodeStyles = {
    background: 'orange',
    color: '#FFF',
    padding: 10,
};




export const IntermediateNode = (node : IntermediateNodeModel) => {


    const handleClick = () => {
        console.log(node.data.onDelete)
        showNodeIntermediateDialog("Bearbeiten sie diese Entnahmestelle", node,
            handleConfirm, () => {/*Nothing to do here*/}, () => node.data.onDelete(node.data.id ?? node.id))
    }

    const handleConfirm = (newNode: IntermediateNodeModel) => {
        node.data.updateNode(newNode)
    }

    return (
        <Tooltip title={<>
            {node.data.annualEnergyDemand ? getOptimizationTooltip(node.data) : <></>}
        </>}>
            <div style={customNodeStyles} onDoubleClick={handleClick}>
                <Handle type="target" position={Position.Top} style={{...customInputHandleStyle}}/>
                <div>{node.data.label}</div>
                <Handle
                    type="source"
                    position={Position.Right}
                    id="a"
                    style={{...customOutputHandleStyle}}
                />
                <Handle
                    type="source"
                    position={Position.Bottom}
                    id="b"
                    style={{...customOutputHandleStyle}}
                />

                <Handle
                    type="source"
                    position={Position.Left}
                    id="c"
                    style={{...customOutputHandleStyle}}
                />
            </div>
        </Tooltip>
    );
};
