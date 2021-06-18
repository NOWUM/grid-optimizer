import {BaseNode, NodeElements} from "../models";
import React from "react";
import {Accordion, AccordionDetails, AccordionSummary} from "@material-ui/core";
// @ts-ignore
import Plot from 'react-plotly.js';

import ExpandMoreIcon from '@material-ui/icons/ExpandMore';


export const OptimizationNodeDetails = ({nodeElements}: { nodeElements: NodeElements }) => {
    return <>
        <h2>Input Nodes</h2>
        <OptimizationAccordion nodes={nodeElements.inputNodes}/>

        <h2>Intermediate Nodes</h2>
        <OptimizationAccordion nodes={nodeElements.intermediateNodes}/>

        <h2>Output Nodes</h2>
        <OptimizationAccordion nodes={nodeElements.outputNodes}/>

    </>
}

export const OptimizationAccordion = ({nodes}: { nodes: BaseNode[] }) => {
    const getAccordion = (n: BaseNode) => {
        return <>
            <Accordion TransitionProps={{unmountOnExit: true}}>
                <AccordionSummary expandIcon={<ExpandMoreIcon/>} aria-controls="panel1a-content" id="panel1a-header">
                    {n.data?.label} ({n.id})
                </AccordionSummary>
                <AccordionDetails>
                    {getGraph(n.optimizedThermalEnergyDemand!, n.connectedPressureLoss!, n.neededPumpPower!)}
                </AccordionDetails>

            </Accordion>
        </>
    }

    const getGraph = (oted: number[], pressureLoss: number[], pumpPower: number[]) => {
        const plotDataHeat = [
            {
                y: oted,
                type: "scattergl",
                mode: 'lines+markers',
                marker: {color: 'red'},
                name: "Angeschlossener Wärmebedarf [Wh]"
            }]
        const plotDataPressure = [
            {
                y: pressureLoss,
                type: "scattergl",
                mode: 'lines+markers',
                marker: {color: 'red'},
                name: "Druckverluste [Bar]"
            }]

        const plotDataPumpPower = [
            {
                y: pumpPower,
                type: "scattergl",
                mode: 'lines+markers',
                marker: {color: 'red'},
                name: "Benötigte Pumpleistung [Wh]"
            }]

        return <div style={{justifyContent: 'center', flexDirection: 'row', display: 'flex'}}>
            <Plot
            data={plotDataHeat}
            style={{width: '100%', height: '100%'}}
            layout={{autosize: true, title: 'Angeschlossener Wärmebedarf [Wh]', xaxis: {title: 'Stunde im Jahr'}}}/>
            <Plot
                data={plotDataPressure}
                style={{width: '100%', height: '100%'}}
                layout={{autosize: true, title: 'Druckverluste [Bar]', xaxis: {title: 'Stunde im Jahr'}}}/>
            <Plot
                data={plotDataPumpPower}
                style={{width: '100%', height: '100%'}}
                layout={{autosize: true, title: 'Benötigte Pumpleistung [Wh]', xaxis: {title: 'Stunde im Jahr'}}}/>
        </div>
    }

    return <>{nodes.filter(n => n.optimizedThermalEnergyDemand)
        .map(n => getAccordion(n))
    } </>
}
