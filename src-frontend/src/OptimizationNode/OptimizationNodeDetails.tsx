import {BaseNode, NodeElements, Pipe} from "../models";
import React, {MutableRefObject, useEffect, useRef} from "react";
import {Accordion, AccordionDetails, AccordionSummary} from "@material-ui/core";
// @ts-ignore
import Plot from 'react-plotly.js';

import ExpandMoreIcon from '@material-ui/icons/ExpandMore';


export const OptimizationNodeDetails = ({
                                            nodeElements,
                                            pipes,
                                            activeId
                                        }: { nodeElements: NodeElements, pipes: Pipe[], activeId?: string }) => {
    let myRef = useRef<HTMLInputElement>()

    useEffect(() => {
        if (activeId) {
            const offsetContainer: number = document.getElementById("first-optimization-accordion")?.offsetTop!;
            document.getElementById("optimization-panel")?.scrollTo({
                behavior: 'smooth',
                top: myRef?.current?.offsetTop! - offsetContainer
            });
        }
    }, [])
    return <>
        <h2>Input Nodes</h2>
        <OptimizationAccordionNode nodes={nodeElements.inputNodes} activeId={activeId} myRef={myRef}
                                   id={"first-optimization-accordion"}/>

        <h2>Intermediate Nodes</h2>
        <OptimizationAccordionNode nodes={nodeElements.intermediateNodes} activeId={activeId} myRef={myRef}/>

        <h2>Output Nodes</h2>
        <OptimizationAccordionNode nodes={nodeElements.outputNodes} activeId={activeId} myRef={myRef}/>

        <h2>Pipes</h2>
        <OptimizationAccordionPipe pipes={pipes} activeId={activeId} myRef={myRef}/>
    </>
}

export const OptimizationAccordionNode = ({
                                              nodes,
                                              activeId,
                                              myRef,
                                              id
                                          }: { nodes: BaseNode[], activeId?: string, myRef: MutableRefObject<any>, id?: string }) => {
    const getAccordionNode = (n: BaseNode, defaultExpanded: boolean) => {
        return <>
            <Accordion TransitionProps={{unmountOnExit: true}} defaultExpanded={defaultExpanded}
                       ref={defaultExpanded ? myRef : undefined} id={id}>
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
        .map(n => getAccordionNode(n, n.id === activeId))
    }</>
}


export const OptimizationAccordionPipe = ({
                                              pipes,
                                              activeId,
                                              myRef
                                          }: { pipes: Pipe[], activeId?: string, myRef: MutableRefObject<any> }) => {
    const getAccordionPipe = (p: Pipe, defaultExpanded: boolean) => {
        return <>
            <Accordion TransitionProps={{unmountOnExit: true}} defaultExpanded={defaultExpanded}
                       ref={defaultExpanded ? myRef : undefined}>
                <AccordionSummary expandIcon={<ExpandMoreIcon/>} aria-controls="panel1a-content" id="panel1a-header">
                    {p.data?.label} ({p.id})
                </AccordionSummary>
                <AccordionDetails>
                    {getGraph(p)}
                </AccordionDetails>

            </Accordion>
        </>
    }

    const getGraph = (p: Pipe) => {
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
                    autosize: true, title: "Druckverlust in Rohrleitung [Bar]", xaxis: {title: 'Stunde im Jahr'}, legend: {
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
        {
            pipes.filter((p: Pipe) => p.diameter)
                .map(p => getAccordionPipe(p, p.id === activeId))
        }</>
}
