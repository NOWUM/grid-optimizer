import {confirmAlert} from "react-confirm-alert";
import 'react-confirm-alert/src/react-confirm-alert.css';
import {Button, Grid, TextField} from "@material-ui/core";
import React from "react";
//import "./confirmation-overlay.css"

export const showSplitEdgeDialog = (message: string, onConfirm: () => void, onAbort: () => void) => {

    confirmAlert({customUI: customUiOptions => <EdgeForm/>})
}


const EdgeForm = () => {
    return <Grid
        container
        direction="row"
        justify="center"
        alignItems="center"
    >
        <Grid container
              direction="row" item xs={12} spacing={3}>
            <Grid item xs={6}>
                <TextField id="standard-basic" label="Leitungslänge [m]" type="number" placeholder="127.30"/>
            </Grid>
        </Grid>
        <Grid container
              direction="row" item xs={12} spacing={3}>
            <Grid item xs={4}>
                <Button >Bestätigen</Button>
            </Grid>
            <Grid item xs={4}>
                <Button>Abbruch</Button>
            </Grid>
        </Grid>

    </Grid>
}
