import {Button, Grid, InputLabel, TextField} from "@material-ui/core";
import {TemperatureDropdown} from "./MetaData/TemperatureDropdown";
import React, {useState} from "react";
import {DEFAULT_GRID_SPACING} from "./utils/defaults";
import {LoadProfileSelect} from "./Components/LoadProfileSelect";

export const FormulaCheck = () => {
    const [energyDemand, setEnergyDemand] = useState(1);
    const [temperatureKey, setTemperatureKey] = useState("");
    const [loadProfileName, setLoadProfileName] = useState("")

    return <>

        <Grid container
              direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={6}
                  alignContent="center"
                  justify="center">
                <InputLabel>Energiebedarf</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="number" variant="outlined" placeholder="127.30"
                           value={energyDemand}
                           onChange={(e) => setEnergyDemand(parseFloat(e.target.value))}/>

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
                <Button variant="contained" onClick={() => {
                }}>
                    Bestätigen
                </Button>
            </Grid>
        </Grid>
    </>
}
