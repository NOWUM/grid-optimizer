import {Button} from "@material-ui/core";
import React from "react";
import "./optimization-button.css";
import {
    BaseNode,
    Costs,
    HotWaterGrid,
    InputNode,
    IntermediateNode,
    NodeElements,
    OptimizationMetadata,
    OptimizationRequest,
    OptimizationResult,
    OptimizedNode,
    OutputNode,
    Pipe, PipeOptimization
} from "../../models";
import {baseUrl} from "../../utils/utility";
import {trackPromise} from "react-promise-tracker";

interface Properties {
    grid: HotWaterGrid,
    optimizationMetadata: OptimizationMetadata,
    setCosts: (c: Costs) => void,
    setNodeElements: (n: NodeElements) => void,
    setPipes: (p: Pipe[]) => void
}


export const OptimizeButton = ({grid, optimizationMetadata, setCosts, setPipes, setNodeElements}: Properties) => {


    const optimize = () => {
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
                    return response.json();
                }).then(res => onOptimize(res as OptimizationResult))
                .catch(e => {
                    console.error(e)
                }));
    }

    const onOptimize = (res: OptimizationResult) => {
        console.log(res)
        setCosts(res.costs)

        const newPipes = grid.pipes.map(p => {
            const opt: PipeOptimization = res.optimizedPipes.find(el => el.pipeId === p.id) ?? {};
            const {diameter, volumeFlow, pipeHeatLoss, pipePressureLoss,totalPressureLoss,totalPumpPower} = opt;
            const isCritical = !!res.criticalPath.find(el => el === p.id );  // if this id is part of the critical path its true, otherwise its false
            return {...p, diameter, isCritical, volumeFlow, pipeHeatLoss, pipePressureLoss,totalPressureLoss,totalPumpPower,
                data: {...p.data, diameter, isCritical, volumeFlow, pipeHeatLoss, pipePressureLoss,totalPressureLoss,totalPumpPower}}
        })

        setNodeElements(mapResultToNodeElements(res))

        setPipes(newPipes)
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
            return {...n,
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
