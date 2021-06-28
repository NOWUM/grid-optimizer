import React from "react";
import {customInputHandleStyle, getOptimizationTooltip, handleNodeCtrlClick} from "./InputNode";
import {Handle, Position} from "react-flow-renderer";
import {showNodeOutputDialog} from "../ReactFlow/Overlays/NodeContextOverlay";
import {OutputNode as OutputNodeModel} from "../models";
import {Tooltip} from "@material-ui/core";

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
        console.log(node.data.onDelete)
        showNodeOutputDialog("Bearbeiten sie diese Entnahmestelle", getOutputNode(),
            handleConfirm, () => {/*Nothing to do here*/}, () => node.data.onDelete(node.data.id ?? node.id))
    }

    const handleConfirm = (newNode: OutputNodeModel) => {
         node.data.updateNode(newNode)
    }

    return (<Tooltip title={<>
            Wärmebedarf: {node.data.thermalEnergyDemand} kWh<br/>
            Druckverlust: {node.data.pressureLoss} Bar <br/>
            Lastprofil: {node.data.loadProfileName} <br/>
            Replicas: {node.data.replicas} <br/>

            {node.data.annualEnergyDemand? getOptimizationTooltip(node.data): <></>}
        </>}>
            <div style={customNodeStyles} onDoubleClick={handleClick} onClick={() => handleNodeCtrlClick(node)}>
                <Handle type="target" position={Position.Top} style={{...customInputHandleStyle}}/>
                <div>{node?.data.label}</div>
            </div>
        </Tooltip>
    );
};
