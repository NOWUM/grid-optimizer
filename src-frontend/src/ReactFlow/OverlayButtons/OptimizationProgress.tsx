import {OptimizationStatusResponse} from "../../models/dto-models";
import {Grid} from "@material-ui/core";
import {DEFAULT_GRID_SPACING} from "../../utils/defaults";
import React from "react";
import "./optimization-progress.css"
import {OptimizationStatusIndicator} from "../../OptimizationNode/OptimizationStatusIndicator";

interface Properties {
    optimizationsStatus?: OptimizationStatusResponse,
    optimizationStarted?: Date
}

export const OptimizationProgress = ({optimizationsStatus, optimizationStarted}: Properties) => {

    if (!optimizationsStatus) {
        return <></>
    }

    const getWaitingTime = (start?: Date) => {
        if(!start){
            return undefined
        }

        const now = new Date()
        // @ts-ignore
        const diff = now - start;
        return Math.floor(diff/1000)
    }

    const osData = [{
        label: "ID",
        value: optimizationsStatus?.id
    }, {
        label: "Anzahl der Checks",
        value: optimizationsStatus?.numberOfChecks
    }, {
        label: "Anzahl der Updates",
        value: optimizationsStatus?.numberOfUpdates
    }, {
        label: "Wartezeit",
        value: getWaitingTime(optimizationStarted) ? `${getWaitingTime(optimizationStarted)} Sek`: "nicht gestartet"
    }]

    const getCostElement = (label: string, value: (number | string)) => {
        return <Grid container
                     direction="row"
                     alignContent="center"

                     justify="space-between" item xs={12} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={2} style={{textAlign: "start"}}>
                {label}:
            </Grid>

            <Grid item xs={6} style={{textAlign: "end"}}>
                {value}
            </Grid>
        </Grid>
    }


    return <div className={"optimization-progress"}>
        <OptimizationStatusIndicator status={optimizationsStatus?.completed}/>
        <Grid
            container
            direction="row"
            justify="center"
            alignItems="center"
            spacing={DEFAULT_GRID_SPACING}
        >
            {osData.map(c => getCostElement(c.label, c.value))}
        </Grid>
    </div>
}
