import {Button} from "@material-ui/core";
import React from "react";
import {notify} from "../Overlays/Notifications";
import "./optimization-button.css";
import {HotWaterGrid, OptimizationMetadata, OptimizationRequest} from "../../models";
import {baseUrl} from "../../utils/utility";
import {ResultCode} from "../FlowContainer";

export const OptimizeButton = ({grid, optimizationMetadata}: {grid: HotWaterGrid, optimizationMetadata: OptimizationMetadata}) => {


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
                response.text().then((text) => {
                    if (text) {
                        notify(text)
                    }
                })
                return response.status
            }).then((status) => {
            return status === ResultCode.OK
        })
            .catch(e => {
                return false
            });
    }


    const getOptimizationRequest = (): OptimizationRequest => {
        return {grid, ...optimizationMetadata}
    }


    return <div className={"optimize-button"}>
        <Button onClick={optimize}>Optimiere Netz</Button>
    </div>
}
