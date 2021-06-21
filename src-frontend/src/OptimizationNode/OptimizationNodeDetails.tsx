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
                    {getGraph(n)}
                </AccordionDetails>

            </Accordion>
        </>
    }

    const getGraph = (n: BaseNode) => {
        const plotDataHeat = [
            {
                x: "date",
                y: n.optimizedThermalEnergyDemand!,
                type: "scattergl",
                marker: {color: 'red'},
                name: "Angeschlossener Wärmebedarf [Wh]"
            }]
        const plotDataPressure = [
            {
                y: n.connectedPressureLoss!,
                type: "scattergl",
                marker: {color: 'red'},
                name: "Druckverluste [Bar]"
            }]

        const plotDataPumpPower = [
            {
                y: n.neededPumpPower,
                type: "scattergl",
                marker: {color: 'red'},
                name: "Benötigte Pumpleistung [Wh]"
            }]

        const plotFlowTemperature = [
            {
                y: n.flowOutTemperature,
                type: "scatter",
                marker: {color: 'blue'},
                name: "Rücklauftemperatur [Wh]"
            }, {
                y: n.flowInTemperature,
                type: "scatter",
                fill: 'tonexty',
                marker: {color: 'red'},
                name: "Vorlauftemperatur [Wh]"
            },
        ]

        return <div
            style={{justifyContent: 'center', flexDirection: 'row', flexWrap: "wrap", display: 'flex', width: "100%"}}>

            <Plot
                data={plotDataHeat}
                style={{width: '500px', height: '400px'}}
                layout={{
                    autosize: true,
                    title: 'Angeschlossener Wärmebedarf [Wh]',
                    xaxis: {title: 'Stunde im Jahr'},
                    legend: {
                        x: 1,
                        xanchor: 'right',
                        y: 1
                    }
                }}/>
            <Plot
                data={plotDataPressure}
                style={{width: '500px', height: '400px'}}
                layout={{
                    autosize: true, title: 'Druckverluste [Bar]', xaxis: {title: 'Stunde im Jahr'}, legend: {
                        x: 1,
                        xanchor: 'right',
                        y: 1
                    }
                }}/>
            <Plot
                data={plotDataPumpPower}
                style={{width: '500px', height: '400px'}}
                layout={{
                    autosize: true, title: 'Benötigte Pumpleistung [Wh]', xaxis: {title: 'Stunde im Jahr'}, legend: {
                        x: 1,
                        xanchor: 'right',
                        y: 1
                    }
                }}/>

            <Plot
                data={plotFlowTemperature}
                style={{width: '650px', height: '400px'}}
                layout={{
                    autosize: true, title: 'Wasser Temperatur [°C]', xaxis: {title: 'Stunde im Jahr'}
                }}/>
        </div>
    }

    return <>{nodes.filter(n => n.optimizedThermalEnergyDemand)
        .map(n => getAccordion(n))
    } </>
}
