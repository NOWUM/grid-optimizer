import {confirmAlert} from "react-confirm-alert";
import React from "react";
import {NodeType} from "../models";
import {Button, FormLabel, Grid, TextField, Typography} from "@material-ui/core";

export const showNodeDialog = (message: string,
                               onConfirm: (id: string, length1: number, length2: number) => void,
                               onAbort: () => void,
                               type: NodeType, id: string) => {

    confirmAlert({
        customUI: ({onClose}) => <OutputNodeForm message={message}
                                                 onConfirm={(id: string, length1: number, length2: number) => {
                                                     onConfirm(id, length1, length2)
                                                     onClose()
                                                 }} onAbort={() => {
            onAbort()
            onClose()
        }} type={type} id={id}
        />
    })
}

export const showNodeOutputDialog = (message: string,
                                     onConfirm: (id: string, length1: number, length2: number) => void,
                                     onAbort: () => void,
                                     id: string) => {

    showNodeDialog(message, onConfirm, onAbort, NodeType.OUTPUT_NODE, id)
}

export const OutputNodeForm = ({message, onConfirm, onAbort, type, id}: {
    message: string
    onConfirm: (id: string, length1: number, length2: number) => void,
    onAbort: () => void,
    type: NodeType,
    id: string
}) => {
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
                    {message}
                </Typography>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <FormLabel> Leitungs-ID: {id} </FormLabel>
            </Grid>
        </Grid>
        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Warmwasserbedarf [Kwh]" type="number" placeholder="127.30"/>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Heizfläche [m&sup2;]" type="number" placeholder="127.30"/>
            </Grid>
        </Grid>

        <Grid container direction="row" item xs={7} spacing={1}>
            <Grid item xs={4}>
                <Button onClick={() => {
                    onConfirm("1", 1, 1)
                }}>
                    Bestätigen
                </Button>
            </Grid>
            <Grid item xs={6}>
                <Button onClick={() => {
                    onAbort()
                }}>
                    Abbruch
                </Button>
            </Grid>
        </Grid>

    </Grid>
}
