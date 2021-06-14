import {confirmAlert} from "react-confirm-alert";
import 'react-confirm-alert/src/react-confirm-alert.css';
import { Grid, TextField} from "@material-ui/core";
import React, {useState} from "react";
import {FormSkeleton} from "./FormSkeleton";
import {generateUniqueID} from "web-vitals/dist/modules/lib/generateUniqueID";
import {notify} from "./Notifications";



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
    const [length1, setLength1] = useState("");

    const getLength1 = (): number => {
        // @ts-ignore
        if(!isNaN(length1)){
            return parseFloat(length1)
        } else {
            return 0.0
        }
    }

    const handleConfirm = () => {
        // @ts-ignore
        if(!isNaN(length1) && length1 > 0.0) {
            onConfirm(id ?? generateUniqueID(), getLength1(), 1)
        } else {
            notify("Länge muss eine positive Zahl sein")
        }
    }

    return <FormSkeleton id={id} message={message} onConfirm={() => handleConfirm()} onAbort={onAbort}>
        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Leitungslänge 1 [m]" type="number" placeholder="127.30"
                           value={length1} onChange={(e) => setLength1(e.target.value)}/>
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


