import {Costs} from "../models/models";
import {Grid} from "@material-ui/core";
import {DEFAULT_GRID_SPACING} from "../utils/defaults";

export const CostView = ({costs}: {costs?: Costs}) => {

    if(!costs){
        return <></>
    }

    const moneyFormatter = (val: number) => `${val?.toFixed(2) ?? "0.00"}€`

    const costsData = [{
        label: "Investitionskosten Leitungen (Gesamt)",
        value: costs?.pipeInvestCostTotal
    }, {
        label: "Investitionskosten Leitungen (Annuität)",
        value: costs?.pipeInvestCostAnnuity
    }, {
        label: "Betriebskosten Leitungen",
        value: costs?.pipeOperationCost
    }, {
        label: "Investitionskosten Pumpe (Gesamt)",
        value: costs?.pumpInvestCostTotal
    }, {
        label: "Investitionskosten Pumpe (Annuität)",
        value: costs?.pumpInvestCostAnnuity
    }, {
        label: "Betriebskosten Pumpen",
        value: costs?.pumpOperationCost
    }, {
        label: "Betriebskosten Wärmeverlust",
        value: costs?.heatLossCost
    }, {
        label: "Gesamtkosten pro Jahr",
        value: costs?.totalPerYear
    }]

    const getCostElement = (label: string, value: number) => {
        return <div style={{width: "100%", display: "flex", justifyContent: "center"}}>
            <div style={{maxWidth: "700px", width: "100%"}}>
                <Grid container
                      direction="row"
                      alignContent="center"
                      item xs={12} spacing={DEFAULT_GRID_SPACING}>
                    <Grid item xs={6} style={{textAlign: "start"}}>
                        {label}:
                    </Grid>

                    <Grid item xs={6} style={{textAlign: "end"}}>
                        {moneyFormatter(value)}
                    </Grid>
                </Grid>
            </div>
        </div>
    }


    return <div className={"costs"}>
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
