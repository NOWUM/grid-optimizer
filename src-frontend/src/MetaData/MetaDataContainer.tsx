import React, {Dispatch, SetStateAction} from "react";
import {Button, Grid, InputLabel, TextField, Typography} from "@material-ui/core";
import {TemperatureDropdown} from "./TemperatureDropdown";
import {OptimizationMetadata} from "../models";

interface Properties {
    temperatureKey: string,
    setTemperatureKey: Dispatch<SetStateAction<string>>
    optimizationMetadata: OptimizationMetadata,
    setOptimizationMetadata: (om: OptimizationMetadata) => void
}

export const MetaDataContainer = ({temperatureKey, setTemperatureKey, optimizationMetadata, setOptimizationMetadata}: Properties) => {

    const {
        gridInvestCostTemplate,
        gridOperatingCostTemplate,
        pumpInvestCostTemplate,
        heatGenerationCost,
        lifespanOfResources,
        wacc,
        electricityCost,
        electricalEfficiency,
        hydraulicEfficiency,
        insulationThickness
    } = optimizationMetadata;

    const SPACING = 3

    const dispatchChange = (val: string, prop: string) => {
        const opt = {...optimizationMetadata};
        let valToInsert: (string|number) = val;
        if (isNumberProp(prop)) {
            if(!Number.isNaN(val) && val !== "") {
                valToInsert = getAsNumber(val);
                // @ts-ignore
                opt[prop] = valToInsert;
                setOptimizationMetadata(opt)
            }
        } else {
            // @ts-ignore
            opt[prop] = valToInsert;
            setOptimizationMetadata(opt)
        }
    }

    const isNumberProp = (prop: string) => {
        const numberProps = [
            "heatGenerationCost",
            "lifespanOfResources",
            "wacc",
            "electricityCost",
            "electricalEfficiency",
            "hydraulicEfficiency",
            "insulationThickness"]
        return numberProps.includes(prop)
    }

    const getAsNumber = (val: string): number => {
        return Number.parseFloat(val);
    }

    return <Grid
        container
        direction="row"
        justify="center"
        alignItems="center"
        spacing={SPACING}
    >
        <Grid container
              direction="row"
              alignContent="center"
              justify="center" item xs={7} spacing={SPACING}>
            <Grid item xs={12}>
                <Typography className={"header-form"} color="textSecondary" gutterBottom>
                    Hier kannst du gegebenenfalls die Meta Daten anpassen
                </Typography>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={6}
                  alignContent="center"
                  justify="center">
                <InputLabel>Isolationsdicke</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="number" variant="outlined" placeholder="127.30"
                           value={insulationThickness}
                           onChange={(e) => dispatchChange(e.target.value, "insulationThickness")}/>

            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={6}>
                <InputLabel>Netzinvestitionskosten Template (f(Durchmesser) = [€/m])</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="text" variant="outlined" placeholder="3*d"
                           value={gridInvestCostTemplate}
                           onChange={(e) => dispatchChange(e.target.value, "gridInvestCostTemplate")}/>

            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={6}>
                <InputLabel>Netzoperationskosten Template (f(Netzinvestitionskosten) = [€/a])</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="text" variant="outlined" placeholder="3*cost"
                           value={gridOperatingCostTemplate}
                           onChange={(e) => dispatchChange(e.target.value, "gridOperatingCostTemplate")}/>

            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={6}>
                <InputLabel>Pumpeninvestitionskosten Template (f(Leistung) = [€/kW])</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="text" variant="outlined" placeholder="3*d"
                           value={pumpInvestCostTemplate}
                           onChange={(e) => dispatchChange(e.target.value, "pumpInvestCostTemplate")}/>

            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={6}>
                <InputLabel>Heizkosten [€/kWh])</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="text" variant="outlined" placeholder="0.12"
                           value={heatGenerationCost}
                           onChange={(e) => dispatchChange(e.target.value, "heatGenerationCost")}/>

            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={6}>
                <InputLabel>Lebensspanne der Ressourcen [a]</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="text" variant="outlined" placeholder="25"
                           value={lifespanOfResources}
                           onChange={(e) => dispatchChange(e.target.value, "lifespanOfResources")}/>

            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={6}>
                <InputLabel>Weighted Average Cost of Capital [%]</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="text" variant="outlined" placeholder="15"
                           value={wacc}
                           onChange={(e) => dispatchChange(e.target.value, "wacc")}/>

            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <h3>Pumpen</h3>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={6}>
                <InputLabel>Elektrizitätskosten Pumpstation [ct/kWh]</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="text" variant="outlined" placeholder="15"
                           value={electricityCost}
                           onChange={(e) => dispatchChange(e.target.value, "electricityCost")}/>

            </Grid>
        </Grid>
        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={6}>
                <InputLabel>Elektrische Effizienz (Pumpe)</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="text" variant="outlined" placeholder="15"
                           value={electricalEfficiency}
                           onChange={(e) => dispatchChange(e.target.value, "electricalEfficiency")}/>

            </Grid>
        </Grid>
        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={6}>
                <InputLabel>Hydraulische Effizienz (Pumpe)</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="text" variant="outlined" placeholder="15"
                           value={hydraulicEfficiency}
                           onChange={(e) => dispatchChange(e.target.value, "hydraulicEfficiency")}/>

            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={6}>
                <InputLabel>Temperaturreihe</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TemperatureDropdown temperatureKey={temperatureKey} setTemperatureKey={setTemperatureKey}/>
            </Grid>
        </Grid>

        <Grid container direction="row" item xs={7} spacing={SPACING}>
            <Grid item xs={12}>
                <Button variant="contained" onClick={() => {
                }}>
                    Bestätigen
                </Button>
            </Grid>
        </Grid>

    </Grid>
}

