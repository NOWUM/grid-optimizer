import {Handle, Position} from "react-flow-renderer";
import {Tooltip} from "@material-ui/core";
import React from "react";
import {InputNode as InputNodeModel} from "../models";
import {showNodeInputDialog} from "../Overlays/NodeContextOverlay";


const customNodeStyles = {
    background: 'green',
    color: '#FFF',
    padding: 10,
};

export const customInputHandleStyle = {
    background: "red", borderRadius: 0
}

export const customOutputHandleStyle = {
    background: "blue", borderRadius: 0
}

export interface CustomNodeDate {
    label: string | Element
}



export const InputNode = (node: InputNodeModel) => {

    const getInputNode = (): InputNodeModel => {
        const newNode = {...node}
        newNode.flowTemperatureTemplate = newNode.data.flowTemperatureTemplate
        newNode.returnTemperatureTemplate = newNode.data.returnTemperatureTemplate
        return newNode
    }

    const handleClick = () => {
        showNodeInputDialog("Bearbeiten sie diesen Einspeisepunkt", getInputNode(),
            (newNode) => {
            console.log(newNode)
            node.data.updateNode(newNode)}, () => {/*Nothing to do here*/})
    }

    return (
        <Tooltip title={<>
            Formel Vorlauftemperatur: {node.data.flowTemperatureTemplate}<br/>
            Formel Rücklauftemperatur: {node.data.returnTemperatureTemplate}
        </>}>
            <div style={customNodeStyles} onDoubleClick={handleClick}>
                <Handle
                    type="source"
                    position={Position.Bottom}
                    id="a"
                    style={{...customOutputHandleStyle}}
                />
                <div>{node.data.label}</div>
            </div>
        </Tooltip>
    );
};
