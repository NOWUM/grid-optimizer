import {Button, Grid, InputLabel, TextField} from "@material-ui/core";
import {TemperatureDropdown} from "./MetaData/TemperatureDropdown";
import React, {useState} from "react";
import {DEFAULT_GRID_SPACING} from "./utils/defaults";
import {LoadProfileSelect} from "./Components/LoadProfileSelect";
import {HeatDemand, HeatDemandResult} from "./models";
import {notify} from "./Overlays/Notifications";
import {trackPromise} from "react-promise-tracker";
import {baseUrl} from "./utils/utility";
// @ts-ignore
import Plot from 'react-plotly.js';

export const FormulaCheck = () => {
    const [thermalEnergyDemand, setThermalEnergyDemand] = useState(1);
    const [temperatureKey, setTemperatureKey] = useState("");
    const [loadProfileName, setLoadProfileName] = useState("")
    const [heatDemandResult, setHeatDemandResult] = useState<HeatDemandResult | undefined>(undefined)

    const getHeatDemand = () => {
        if(loadProfileName === "" || temperatureKey === "") {
            notify("Lastprofil und Temperaturzeitreihe müssen gesetzt werden.")
        } else {
            fetchHeatDemand()
        }
    }

    const getHeatDemandDTO = (): HeatDemand => {
        return {temperatureSeries: temperatureKey, loadProfileName, thermalEnergyDemand }
    }

    const configuration = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(getHeatDemandDTO())
    }

    const fetchHeatDemand = () => {
        trackPromise(
            fetch(`${baseUrl}/api/heatdemand`, configuration)
                .then(response => {
                    return response.json()
                }).then(p => setHeatDemandResult(p))
                .catch(e => {
                    return false}));
    }

    const getPlotData = () => {
        return [
            {
                y: heatDemandResult?.curve,
                type: "scattergl",
                mode: 'lines+markers',
                marker: {
                    color: 'rgb(253 126 20)'
                },
                name: "Energie Heat Demand [kWh]"
            },
            {
                y: heatDemandResult?.temperature,
                type: "scattergl",
                mode: 'lines+markers',
                marker: {
                    color: 'rgb(100 126 20)'
                },
                name: "Temperature [°C]"
            },
            {
                y: heatDemandResult?.allokation,
                type: "scattergl",
                mode: 'lines+markers',
                marker: {
                    color: 'rgb(100 50 20)'
                },
                name: "Allokation [°C]"
            },
            {
                y: heatDemandResult?.dailyHeatCurve,
                type: "scattergl",
                mode: 'lines+markers',
                marker: {
                    color: 'rgb(100 126 200)'
                },
                name: "Daily Heat Curve"
            }
        ]
    }

    return <>

        <Grid container
              direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={6}
                  alignContent="center"
                  justify="center">
                <InputLabel>Energiebedarf [kWh/Jahr]</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="number" variant="outlined" placeholder="127.30"
                           value={thermalEnergyDemand}
                           onChange={(e) => setThermalEnergyDemand(parseFloat(e.target.value))}/>

            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={6}>
                <InputLabel>Temperaturreihe</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TemperatureDropdown temperatureKey={temperatureKey} setTemperatureKey={setTemperatureKey}/>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={6}>
                Standardlastprofil
            </Grid>

            <Grid item xs={6}>
                <LoadProfileSelect value={loadProfileName} onValueChange={setLoadProfileName} />
            </Grid>
        </Grid>



        <Grid container direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={12}>
                <Button variant="contained" onClick={getHeatDemand}>
                    Bestätigen
                </Button>
            </Grid>

        </Grid>
        <Plot
            data={getPlotData()}
            style={{ width: '100%', height: '100%', color:"blue" }}
            layout={ {autosize: true, title: 'Wärmebedarf', xaxis: { title: 'Stunde im Jahr' } } }
        />
    </>
}
