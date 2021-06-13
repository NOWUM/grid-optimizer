import {Button} from "@material-ui/core";
import React from "react";
import "./optimization-button.css";
import {Costs, HotWaterGrid, OptimizationMetadata, OptimizationRequest, OptimizationResult, Pipe} from "../../models";
import {baseUrl} from "../../utils/utility";

interface Properties {
    grid: HotWaterGrid,
    optimizationMetadata: OptimizationMetadata,
    setCosts: (c: Costs) => void,
    setPipes: (p: Pipe[]) => void
}


export const OptimizeButton = ({grid, optimizationMetadata, setCosts, setPipes}: Properties) => {


    const optimize = () => {
        const configuration = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(getOptimizationRequest())
        }

        fetch(`${baseUrl}/api/optimize`, configuration)
            .then(response => {
                return response.json();
            }).then(res => onOptimize(res as OptimizationResult))
            .catch(e => {
                console.error(e)
            });
    }

    const onOptimize = (res: OptimizationResult) => {
        setCosts(res.costs)

        const newPipes = grid.pipes.map(p => {
            const diameter = res.optimizedPipes.find(el => el.pipeId === p.id)?.diameter
            console.log(diameter)
            return {...p, diameter, data: {...p.data, diameter}}
        })

        setPipes(newPipes)
    }


    const getOptimizationRequest = (): OptimizationRequest => {
        return {grid, ...optimizationMetadata}
    }


    return <div className={"optimize-button"}>
        <Button onClick={optimize}>Optimiere Netz</Button>
    </div>
}
