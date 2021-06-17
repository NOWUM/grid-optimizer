import React from "react";
import {customInputHandleStyle} from "./InputNode";
import {Handle, Position} from "react-flow-renderer";
import {showNodeOutputDialog} from "../ReactFlow/Overlays/NodeContextOverlay";
import {OutputNode as OutputNodeModel} from "../models";
import {Tooltip} from "@material-ui/core";
import {verifyBackend} from "../ReactFlow/FlowContainer";

const customNodeStyles = {
    background: 'red',
    color: '#FFF',
    padding: 10,
};

export const OutputNode = (node: OutputNodeModel) => {

    const getOutputNode = (): OutputNodeModel => {
        const newNode = {...node}
        newNode.thermalEnergyDemand = newNode.data.thermalEnergyDemand
        newNode.pressureLoss = newNode.data.pressureLoss
        newNode.loadProfileName = newNode.data.loadProfileName
        newNode.replicas = newNode.data.replicas
        return newNode
    }

    const handleClick = () => {
        showNodeOutputDialog("Bearbeiten sie diese Entnahmestelle", getOutputNode(),
            handleConfirm, () => {/*Nothing to do here*/
            })
    }

    const handleConfirm = (newNode: OutputNodeModel) => {
        verifyBackend(node.data.grid).then(b => {
            if (b) {
                node.data.updateNode(newNode)
            }
        })
    }

    return (<Tooltip title={<>
            Wärmebedarf: {node.data.thermalEnergyDemand} kWh<br/>
            Druckverlust: {node.data.pressureLoss} Bar <br/>
            Lastprofil: {node.data.loadProfileName} <br/>
            Replicas: {node.data.replicas}
        </>}>
            <div style={customNodeStyles} onDoubleClick={handleClick}>
                <Handle type="target" position={Position.Top} style={{...customInputHandleStyle}}/>
                <div>{node?.data.label}</div>
            </div>
        </Tooltip>
    );
};
