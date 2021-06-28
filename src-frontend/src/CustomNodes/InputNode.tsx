import {Edge, Handle, Position} from "react-flow-renderer";
import {Tooltip} from "@material-ui/core";
import React, {ReactElement} from "react";
import {BaseNode, InputNode as InputNodeModel, Pipe} from "../models";
import {showNodeInputDialog} from "../ReactFlow/Overlays/NodeContextOverlay";
import {notify} from "../ReactFlow/Overlays/Notifications";


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
        Jährlicher Energiebedarf: {baseNode?.annualEnergyDemand} W/h <br/>
        Maximal benötigte Pumpleistung: {baseNode?.maximalNeededPumpPower} Watt <br/>
        Maximaler Druckverust: {baseNode?.maximalPressureLoss} Bar <br/>
    </>
}


export const handleNodeCtrlClick = (flowElement: (BaseNode | Pipe)) => {
    if(isCtrlyKeyPressed()) {
        if((flowElement.data.annualEnergyDemand || flowElement.data.diameter)){
            flowElement.data.onCtrlClick(flowElement.id)
        }
        else if(!flowElement.data.annualEnergyDemand && !flowElement.data.diameter){
            notify("Für dieses Element ist leider keine Optimierung verfügbar. Optimiere zunächst das Netz.")
        }
    }

}

// @ts-ignore
export const isCtrlyKeyPressed = () => window.event?.ctrlKey

export const InputNode = (node: InputNodeModel) => {

    const getInputNode = (): InputNodeModel => {
        const newNode = {...node}
        newNode.flowTemperatureTemplate = newNode.data.flowTemperatureTemplate
        newNode.returnTemperatureTemplate = newNode.data.returnTemperatureTemplate
        return newNode
    }

    const handleDoubleClick = () => {
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
            <div style={customNodeStyles} onDoubleClick={handleDoubleClick} onClick={() => handleNodeCtrlClick(node)}>
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
