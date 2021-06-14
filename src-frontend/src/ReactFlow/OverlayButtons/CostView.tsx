import {Costs} from "../../models";
import "./cost-view.css"
import {DEFAULT_GRID_SPACING} from "../../utils/defaults";
import {Grid, Typography} from "@material-ui/core";
import React from "react";

export const CostView = ({costs}: {costs?: Costs}) => {

    if(!costs){
        return <></>
    }

    const moneyFormatter = (val: number) => `${val.toFixed(2)}€`

    const costsData = [{
        label: "Investitionskosten Leitungen",
        value: costs?.pipeInvestCost
    }, {
        label: "Betriebskosten Leitungen",
        value: costs?.pipeOperationCost
    }, {
        label: "Investitionskosten Pumpen",
        value: costs?.pumpInvestCost
    },{
        label: "Betriebskosten Pumpen",
        value: costs?.pumpOperationCost
    },{
        label: "Gesamt",
        value: costs?.total
    }]

    const getCostElement = (label: string, value: number) => {
        return <Grid container
              direction="row"
              alignContent="center"

              justify="space-between" item xs={12} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={6} style={{textAlign: "start"}}>
                {label}:
            </Grid>

            <Grid item xs={6} style={{textAlign: "end"}}>
                {moneyFormatter(value)}
            </Grid>
        </Grid>
    }


    return <div className={"cost-view"}>
        <Grid
            container
            direction="row"
            justify="center"
            alignItems="center"
            spacing={DEFAULT_GRID_SPACING}
        >
            {costsData.map(c => getCostElement(c.label, c.value))}
        </Grid>
    </div>
}
