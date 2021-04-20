import {confirmAlert} from "react-confirm-alert";
import 'react-confirm-alert/src/react-confirm-alert.css';
import {Button, CardHeader, FormLabel, Grid, TextField, Typography} from "@material-ui/core";
import React from "react";
import classes from "*.module.css";

export enum EdgeActionType {
    EDIT,
    SPLIT
}


export const showEdgeDialog = (message: string,
                               onConfirm: (id: string, length1: number, length2: number) => void,
                               onAbort: () => void,
                               type: EdgeActionType, id: string) => {

    confirmAlert({
        customUI: ({onClose}) => <EdgeSplitForm message={message} onConfirm={(id: string, length1: number, length2: number) => {
            onConfirm(id, length1, length2)
            onClose()
        }} onAbort={() => {
            onAbort()
            onClose()
        }} type={type} id={id}
        />
    })
}


const EdgeSplitForm = ({message, onConfirm, onAbort, type, id}: {
    message: string
    onConfirm: (id: string, length1: number, length2: number) => void,
    onAbort: () => void,
    type: EdgeActionType,
    id: string
}) => {
    return <Grid
        container
        direction="row"
        justify="center"
        alignItems="center"
    >
        <Grid container
              direction="row" item xs={8} spacing={3}>
            <Grid item xs={6}>
                <Typography className={"header-form"} color="textSecondary" gutterBottom>
                    {message}
                </Typography>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={8} spacing={3}>
            <Grid item xs={6}>
                <FormLabel> Leitungs-ID: {id} </FormLabel>
            </Grid>
        </Grid>
        <Grid container
              direction="row" item xs={8} spacing={3}>
            <Grid item xs={6}>
                <TextField id="standard-basic" label="Leitungslänge 1 [m]" type="number" placeholder="127.30"/>
            </Grid>
        </Grid>
        {type === EdgeActionType.SPLIT ?
            <Grid container
                  direction="row" item xs={8} spacing={3}>
                <Grid item xs={6}>
                    <TextField id="standard-basic" label="Leitungslänge 2 [m]" type="number" placeholder="127.30"/>
                </Grid>
            </Grid> : <></>
        }
        <Grid container direction="row" item xs={8} spacing={3}>
            <Grid item xs={4}>
                <Button onClick={() => {
                    onConfirm("1", 1, 1)
                }}>
                    Bestätigen
                </Button>
            </Grid>
            <Grid item xs={4}>
                <Button onClick={() => {
                    onAbort()
                }}>
                    Abbruch
                </Button>
            </Grid>
        </Grid>

    </Grid>
}

export const showEditEdgeDialog = (message: string,
                                   onConfirm: (id: string, length1: number) => void,
                                   onAbort: () => void,
                                   id: string) => {

    showEdgeDialog(message, onConfirm, onAbort, EdgeActionType.EDIT, id)
}

export const showSplitEdgeDialog = (message: string,
                                    onConfirm: (id: string, length1: number, length2: number) => void,
                                    onAbort: () => void,
                                    id: string) => {

    showEdgeDialog(message, onConfirm, onAbort, EdgeActionType.SPLIT, id)
}


