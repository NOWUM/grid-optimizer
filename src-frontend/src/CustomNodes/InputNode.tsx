import {Handle, Position} from "react-flow-renderer";
import {Tooltip} from "@material-ui/core";
import React, {ReactElement} from "react";
import {BaseNode, InputNode as InputNodeModel} from "../models";
import {showNodeInputDialog} from "../ReactFlow/Overlays/NodeContextOverlay";
import {verifyBackend} from "../ReactFlow/FlowContainer";


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

export const getOptimizationTooltip = (baseNode: BaseNode): ReactElement => {
    return <>
        Jährlicher Energiebedarf: {baseNode?.annualEnergyDemand} Wh/a <br/>
        Maximal benötigte Pumpleistung: {baseNode?.maximalNeededPumpPower} W <br/>
        Maximaler Druckverlust: {baseNode?.maximalPressureLoss} Bar <br/>
    </>
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
            handleConfirm, () => {/*Nothing to do here*/
            }, () => node.data.onDelete(node.data.id ?? node.id))
    }

    const handleConfirm = (newNode: InputNodeModel) => {
        console.log(newNode)
        node.data.updateNode(newNode)
    }

    return (
        <Tooltip title={<>
            Formel Vorlauftemperatur: {node.data.flowTemperatureTemplate}<br/>
            Formel Rücklauftemperatur: {node.data.returnTemperatureTemplate} <br/>
            {node.data.annualEnergyDemand? getOptimizationTooltip(node.data): <></>}
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
