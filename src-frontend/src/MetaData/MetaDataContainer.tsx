import React, {Dispatch, SetStateAction, useState} from "react";
import {Button, Grid, InputLabel, TextField, Typography} from "@material-ui/core";
import {TemperatureDropdown} from "./TemperatureDropdown";

interface Properties {
    temperatureKey: string,
    setTemperatureKey: Dispatch<SetStateAction<string>>
}

export const MetaDataContainer = ({temperatureKey, setTemperatureKey}: Properties) => {


    const [insulationThickness, setInsulationThickness] = useState(0);
    const [pressureDropPerOutput, setPressureDropPerOutput] = useState(0)



    return <Grid
        container
        direction="row"
        justify="center"
        alignItems="center"
    >
        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <Typography className={"header-form"} color="textSecondary" gutterBottom>
                    Hier kannst du gegebenenfalls die Meta Daten anpassen
                </Typography>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={6}>
                <InputLabel>Druckverlust pro Output</InputLabel>
                {/*<TextField id="outlined-basic" label="Isolationsdicke" type="number" variant="outlined" placeholder="127.30"/>*/}
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" variant="outlined" type="number"
                           placeholder="127.30"/>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={6}>
                <InputLabel>Isolationsdicke</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="number" variant="outlined" placeholder="127.30"/>

            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={6}>
                <InputLabel>Temperaturreihe</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TemperatureDropdown temperatureKey={temperatureKey} setTemperatureKey={setTemperatureKey}/>
            </Grid>
        </Grid>

        <Grid container direction="row" item xs={7} spacing={1}>
            <Grid item xs={12}>
                <Button variant="contained" onClick={() => {
                }}>
                    Bestätigen
                </Button>
            </Grid>
        </Grid>

    </Grid>
}

