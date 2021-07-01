import {Button} from "@material-ui/core";
import React, {useEffect} from "react";
import "./optimization-button.css";
import {
    BaseNode,
    Costs,
    HotWaterGrid,
    InputNode,
    IntermediateNode,
    NodeElements,
    OutputNode,
    Pipe,
} from "../../models/models";
import {baseUrl} from "../../utils/utility";
import {trackPromise} from "react-promise-tracker";
import {ResultCode} from "../FlowContainer";
import {notify} from "../Overlays/Notifications";
import {
    OptimizationMetadata,
    OptimizationOverviewResponse,
    OptimizationRequest,
    OptimizationResult,
    OptimizationStatusResponse, OptimizedNode,
    PipeOptimization,
} from "../../models/dto-models";

interface Properties {
    grid: HotWaterGrid,
    optimizationMetadata: OptimizationMetadata,
    setCosts: (c: Costs) => void,
    setNodeElements: (n: NodeElements) => void,
    setPipes: (p: Pipe[]) => void,
    optimizationStatus?: OptimizationStatusResponse,
    setOptimizationStatus: (o: OptimizationStatusResponse) => void,
    setOptimizationStarted: (d: Date) => void
}

let interval: NodeJS.Timer
export const getConfiguration = {
    method: 'GET',
    headers: {
        'Content-Type': 'application/json'
    }
}

export const OptimizeButton = ({
                                   grid, optimizationMetadata, setCosts, setPipes, setNodeElements,
                                   optimizationStatus, setOptimizationStatus, setOptimizationStarted
                               }: Properties) => {
    useEffect(() => {
        console.log("new interval")
        if (optimizationStatus?.id && optimizationStatus?.completed === false) {
            interval = setInterval(fetchUpdate, 1000)
        } else {
            getFinalResult()
        }
        return () => {
            clearInterval(interval)
        }
    }, [optimizationStatus?.completed])

    const optimize = () => {

        setOptimizationStarted(new Date())
        const configuration = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(getOptimizationRequest())
        }
        trackPromise(
            fetch(`${baseUrl}/api/optimize`, configuration)
                .then(response => {
                    if (response.status != ResultCode.OK) {
                        response.text().then(text => {
                            if (text) {
                                notify(text)
                            } else {
                                notify('Unbekannter Fehler bei Aufruf der Optimierung.')
                            }
                        });
                        throw 'Status code not gud.';
                    }
                    return response.json();
                }).then(res => onOptimize(res as OptimizationStatusResponse))
                .catch(e => {
                    console.error(e)
                })
        );
    }

    const fetchUpdate = () => {
        fetch(`${baseUrl}/api/optimize/${optimizationStatus?.id}`, getConfiguration)
            .then(response => {
                if (response.status !== ResultCode.OK) {
                    response.text().then(text => {
                        if (text) {
                            notify(text)
                        } else {
                            notify('Unbekannter Fehler bei Aufruf der Optimierung.')
                        }
                    });
                    throw 'Status code not good.';
                }
                return response.json();
            }).then(res => onOptimize(res as OptimizationStatusResponse))
            .catch(e => {
                console.error(e)
            })
    }

    const getFinalResult = () => {

        fetch(`${baseUrl}/api/optimize/${optimizationStatus?.id}/overview`, getConfiguration)
            .then(response => {
                if (response.status !== ResultCode.OK) {
                    response.text().then(text => {
                        if (text) {
                            notify(text)
                        } else {
                            notify('Unbekannter Fehler bei Aufruf der Optimierung.')
                        }
                    });
                    throw 'Status code not good.';
                }
                return response.json();
            }).then(res => onFinalResult(res as OptimizationOverviewResponse))
            .catch(e => {
                console.error(e)
            })
    }

    const onFinalResult = (res: OptimizationOverviewResponse) => {
        setCosts(res.costs)
        const newPipes = grid.pipes.map(p => {
            const opt: PipeOptimization = res.optimizedPipes.find(el => el.pipeId === p.id) ?? {};
            const {diameter} = opt;
            const isCritical = !!res.criticalPath.find(el => el === p.id);  // if this id is part of the critical path its true, otherwise its false
            const isLongest = !!res.longestPath.find(el => el === p.id);
            return {
                ...p, diameter, isCritical, isLongest,
                data: {...p.data, diameter, isCritical, isLongest}
            }
        })

        setPipes(newPipes)
    }

    const onOptimize = (res: OptimizationStatusResponse) => {
        setOptimizationStatus(res)
        // setNodeElements(mapResultToNodeElements(res))
    }

    const mapResultToNodeElements = (res: OptimizationResult): NodeElements => {
        const inputNodes = mapResultToNodeElement(grid.inputNodes, res.optimizedNodes) as InputNode[]
        const intermediateNodes = mapResultToNodeElement(grid.intermediateNodes, res.optimizedNodes) as IntermediateNode[]
        const outputNodes = mapResultToNodeElement(grid.outputNodes, res.optimizedNodes) as OutputNode[]
        return {inputNodes, intermediateNodes, outputNodes}
    }

    const mapResultToNodeElement = (n: BaseNode[], optNodes: OptimizedNode[]) => {
        return n.map(n => {
            const resultNode: (OptimizedNode | undefined) = optNodes.find(r => r.nodeId === n.id);
            return {
                ...n,
                optimizedThermalEnergyDemand: resultNode?.thermalEnergyDemand,
                connectedPressureLoss: resultNode?.connectedPressureLoss,
                neededPumpPower: resultNode?.neededPumpPower,
                flowInTemperature: resultNode?.flowInTemperature,
                flowOutTemperature: resultNode?.flowOutTemperature,

                annualEnergyDemand: resultNode?.annualEnergyDemand,
                maximalNeededPumpPower: resultNode?.maximalNeededPumpPower,
                maximalPressureLoss: resultNode?.maximalPressureLoss
            }
        })
    }

    const getOptimizationRequest = (): OptimizationRequest => {
        return {grid, ...optimizationMetadata}
    }


    return <div className={"optimize-button"}>
        <Button onClick={optimize}>Optimiere Netz</Button>
    </div>
}
