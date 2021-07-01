import {BaseNode, Costs, NodeElements, NodeOptimization, Pipe} from "../models/models";
import React, {useState} from "react";
import {Accordion, AccordionDetails, AccordionSummary} from "@material-ui/core";
// @ts-ignore
import Plot from 'react-plotly.js';

import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import {baseUrl} from "../utils/utility";
import {ResultCode} from "../ReactFlow/FlowContainer";
import {notify} from "../ReactFlow/Overlays/Notifications";
import {PipeOptimization} from "../models/dto-models";
import {getConfiguration} from "../ReactFlow/OverlayButtons/OptimizeButton";
import {CostView} from "./CostView";
import {XLSXDownload} from "../Filemanagement/XLSXDownload";

interface Properties {
    nodeElements: NodeElements,
    pipes: Pipe[],
    optId: string,
    costs: Costs
}

export const OptimizationDetails = ({nodeElements, pipes, optId, costs}: Properties) => {
    return <>
        <h2>Download Excel</h2>
        <XLSXDownload optId={optId}/>

        <h2>Kosten</h2>
        <CostView costs={costs}/>

        <h2>Input Nodes</h2>
        <OptimizationAccordionNodeContainer nodes={nodeElements.inputNodes} optId={optId}/>

        <h2>Intermediate Nodes</h2>
        <OptimizationAccordionNodeContainer nodes={nodeElements.intermediateNodes} optId={optId}/>

        <h2>Output Nodes</h2>
        <OptimizationAccordionNodeContainer nodes={nodeElements.outputNodes} optId={optId}/>

        <h2>Pipes</h2>
        <OptimizationAccordionPipeContainer pipes={pipes} optId={optId}/>
    </>
}

export const OptimizationAccordionNodeContainer = ({nodes, optId}: { nodes: BaseNode[], optId: string }) => {

    return <>{nodes.map(n => <OptimizationAccordionNode node={n} optId={optId}/>)
    }</>
}

export const OptimizationAccordionNode = ({node, optId}: { node: BaseNode, optId: string }) => {

    const [nodeOptimization, setNodeOptimization] = useState<NodeOptimization | undefined>()

    const fetchGraph = (event: React.ChangeEvent<{}>, expanded: boolean, pid: string) => {
        if (expanded) {
            fetch(`${baseUrl}/api/optimize/${optId}/node/${pid}`, getConfiguration)
                .then(response => {
                    if (response.status !== ResultCode.OK) {
                        response.text().then(text => {
                            if (text) {
                                notify(text)
                            } else {
                                notify('Unbekannter Fehler beim Aufruf der Optimierungsergebnisse.')
                            }
                        });
                        throw 'Status code not good.';
                    }
                    return response.json();
                }).then((res) => setNodeOptimization({...res,
                optimizedThermalEnergyDemand: res.thermalEnergyDemand}))
                .catch(e => {
                    console.error(e)
                })
        }
    }

    const getGraph = (n: NodeOptimization) => {
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

    return <>
        <Accordion TransitionProps={{unmountOnExit: true}}
                   onChange={(event: React.ChangeEvent<{}>, expanded: boolean) => fetchGraph(event, expanded, node.id)}>
            <AccordionSummary expandIcon={<ExpandMoreIcon/>} aria-controls="panel1a-content" id="panel1a-header">
                <b>{node.data?.label}</b> &nbsp; ({node.id})
            </AccordionSummary>
            <AccordionDetails>
                {nodeOptimization ? getGraph(nodeOptimization): <NoDetailsAvailable />}
            </AccordionDetails>

        </Accordion>
    </>
}

export const OptimizationAccordionPipeContainer = ({pipes, optId}: { pipes: Pipe[], optId: string }) => {

    return <>{pipes.filter((p: Pipe) => p.diameter)
        .map(p => <OptimizationAccordionPipe pipe={p} optId={optId}/>)
    }</>
}

export const OptimizationAccordionPipe = ({pipe, optId}: { pipe: Pipe, optId: string }) => {
    const [pipeOptimization, setPipeOptimization] = useState<PipeOptimization | undefined>()

    const fetchGraph = (event: React.ChangeEvent<{}>, expanded: boolean, pid: string) => {
        if (expanded) {
            fetch(`${baseUrl}/api/optimize/${optId}/pipe/${pid}`, getConfiguration)
                .then(response => {
                    if (response.status !== ResultCode.OK) {
                        response.text().then(text => {
                            if (text) {
                                notify(text)
                            } else {
                                notify('Unbekannter Fehler beim Aufruf der Optimierungsergebnisse.')
                            }
                        });
                        throw 'Status code not good.';
                    }
                    return response.json();
                }).then((res: PipeOptimization) => setPipeOptimization(res))
                .catch(e => {
                    console.error(e)
                })
        }
    }

    const getGraph = (p: PipeOptimization) => {
        const plotVolumeFlow = [
            {
                y: p.volumeFlow!,
                type: "scattergl",
                marker: {color: 'red'},
                name: "Volumenstrom in m³/s"
            }]
        const plotPipeHeatLoss = [
            {
                y: p.pipeHeatLoss,
                type: "scattergl",
                marker: {color: 'red'},
                name: "Wärmeverluste [Wh]"
            }]

        const plotPipePressureLoss = [
            {
                y: p.pipePressureLoss,
                type: "scattergl",
                marker: {color: 'red'},
                name: "Druckverlust in Rohrleitung [Bar]"
            }]

        const plotTotalPressureLoss = [
            {
                y: p.totalPressureLoss,
                type: "scatter",
                marker: {color: 'red'},
                name: "Gesamter Druckverlust [Bar]"
            }
        ]

        const plotTotalPumpPower = [
            {
                y: p.totalPumpPower,
                type: "scatter",
                marker: {color: 'red'},
                name: "Benötigte Pumpleistung [W]"
            }
        ]

        return <div
            style={{justifyContent: 'center', flexDirection: 'row', flexWrap: "wrap", display: 'flex', width: "100%"}}>

            <Plot
                data={plotVolumeFlow}
                style={{width: '500px', height: '400px'}}
                layout={{
                    autosize: true,
                    title: "Volumenstrom in m³/s",
                    xaxis: {title: 'Stunde im Jahr'},
                    legend: {
                        x: 1,
                        xanchor: 'right',
                        y: 1
                    }
                }}/>
            <Plot
                data={plotPipeHeatLoss}
                style={{width: '500px', height: '400px'}}
                layout={{
                    autosize: true, title: "Wärmeverluste [Wh]", xaxis: {title: 'Stunde im Jahr'}, legend: {
                        x: 1,
                        xanchor: 'right',
                        y: 1
                    }
                }}/>
            <Plot
                data={plotPipePressureLoss}
                style={{width: '500px', height: '400px'}}
                layout={{
                    autosize: true,
                    title: "Druckverlust in Rohrleitung [Bar]",
                    xaxis: {title: 'Stunde im Jahr'},
                    legend: {
                        x: 1,
                        xanchor: 'right',
                        y: 1
                    }
                }}/>

            <Plot
                data={plotTotalPressureLoss}
                style={{width: '500px', height: '400px'}}
                layout={{
                    autosize: true, title: "Gesamter Druckverlust [Bar]", xaxis: {title: 'Stunde im Jahr'}
                }}/>

            <Plot
                data={plotTotalPumpPower}
                style={{width: '500px', height: '400px'}}
                layout={{
                    autosize: true, title: "Benötigte Pumpleistung [W]", xaxis: {title: 'Stunde im Jahr'}
                }}/>
        </div>
    }

    return <>
        <Accordion onChange={(event: React.ChangeEvent<{}>, expanded: boolean) => fetchGraph(event, expanded, pipe.id)}
                   TransitionProps={{unmountOnExit: true}}>
            <AccordionSummary expandIcon={<ExpandMoreIcon/>} aria-controls="panel1a-content" id="panel1a-header">
                {pipe.data?.label} ({pipe.id})
            </AccordionSummary>
            <AccordionDetails>
                {pipeOptimization ? getGraph(pipeOptimization) : <NoDetailsAvailable/>}
            </AccordionDetails>

        </Accordion>
    </>
}

export const NoDetailsAvailable = () => {
    return <div>Keine Details verfügbar</div>
}
