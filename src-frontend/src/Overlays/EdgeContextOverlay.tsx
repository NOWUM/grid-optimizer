import {confirmAlert} from "react-confirm-alert";
import 'react-confirm-alert/src/react-confirm-alert.css';
import { Grid, TextField} from "@material-ui/core";
import React from "react";
import {FormSkeleton} from "./FormSkeleton";



export enum PipeActionType {
    EDIT,
    SPLIT
}


export const showPipeDialog = (message: string,
                               onConfirm: (id: string, length1: number, length2: number) => void,
                               onAbort: () => void,
                               type: PipeActionType, id: string) => {

    confirmAlert({
        customUI: ({onClose}) => <PipeSplitForm message={message} onConfirm={(id: string, length1: number, length2: number) => {
            onConfirm(id, length1, length2)
            onClose()
        }} onAbort={() => {
            onAbort()
            onClose()
        }} type={type} id={id}
        />
    })
}


const PipeSplitForm = ({message, onConfirm, onAbort, type, id}: {
    message: string
    onConfirm: (id: string, length1: number, length2: number) => void,
    onAbort: () => void,
    type: PipeActionType,
    id: string
}) => {
    return <FormSkeleton id={id} message={message} onConfirm={() => {onConfirm("1", 1, 1)}} onAbort={onAbort}>
        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Leitungslänge 1 [m]" type="number" placeholder="127.30"/>
            </Grid>
        </Grid>
        {type === PipeActionType.SPLIT ?
            <Grid container
                  direction="row" item xs={7} spacing={3}>
                <Grid item xs={12}>
                    <TextField id="standard-basic" label="Leitungslänge 2 [m]" type="number" placeholder="127.30"/>
                </Grid>
            </Grid> : <></>
        }
    </FormSkeleton>
}

export const showEditPipeDialog = (message: string,
                                   onConfirm: (id: string, length1: number) => void,
                                   onAbort: () => void,
                                   id: string) => {

    showPipeDialog(message, onConfirm, onAbort, PipeActionType.EDIT, id)
}

export const showSplitPipeDialog = (message: string,
                                    onConfirm: (id: string, length1: number, length2: number) => void,
                                    onAbort: () => void,
                                    id: string) => {

    showPipeDialog(message, onConfirm, onAbort, PipeActionType.SPLIT, id)
}


