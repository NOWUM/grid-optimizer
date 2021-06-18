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
                    {getGraph(n.optimizedThermalEnergyDemand!)}
                </AccordionDetails>

            </Accordion>
        </>
    }

    const getGraph = (oted: number[]) => {
        const plotData = [
            {
                y: oted,
                type: "scattergl",
                mode: 'lines+markers',
                marker: {color: 'red'},
                name: "Energie Heat Demand [kWh]"
            }]

        return <div><Plot
            data={plotData}
            style={{width: '100%', height: '100%'}}
            layout={{autosize: true, title: 'Maximaler Massenstrom', xaxis: {title: 'Stunde im Jahr'}}}
        /></div>
    }

    return <>{nodes.filter(n => n.optimizedThermalEnergyDemand)
        .map(n => getAccordion(n))
    } </>
}
