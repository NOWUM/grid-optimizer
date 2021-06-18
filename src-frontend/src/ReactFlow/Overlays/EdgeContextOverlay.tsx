import {confirmAlert} from "react-confirm-alert";
import 'react-confirm-alert/src/react-confirm-alert.css';
import {Grid, TextField} from "@material-ui/core";
import React, {useState} from "react";
import {FormSkeleton} from "./FormSkeleton";
import {generateUniqueID} from "web-vitals/dist/modules/lib/generateUniqueID";
import {notify} from "./Notifications";


export const showPipeDialog = (message: string,
                               onConfirm: (id: string, length1: number, coverageHeight: number) => void,
                               onAbort: () => void,
                               id: string, length?: number, coverageHeight?: number,) => {

    confirmAlert({
        customUI: ({onClose}) => <PipeEditForm message={message}
                                               onConfirm={(id: string, length1: number, coverageHeight: number) => {
                                                   onConfirm(id, length1, coverageHeight)
                                                   onClose()
                                               }}
                                               onAbort={() => {
                                                   onAbort()
                                                   onClose()
                                               }} id={id}
                                               propLength={length}
                                               propCoverageHeight={coverageHeight}
        />
    })
}


const PipeEditForm = ({message, onConfirm, onAbort, id, propLength, propCoverageHeight }: {
    message: string
    onConfirm: (id: string, length1: number, coverageHeight: number) => void,
    onAbort: () => void,
    id: string,
    propLength?: number,
    propCoverageHeight?: number,
}) => {
    const [length1, setLength1] = useState(`${propLength}` ?? "");

    const getLength1 = (): number => {
        // @ts-ignore
        if(!isNaN(length1)){
            return parseFloat(length1)
        } else {
            return 0.0
        }
    }

    const [coverageHeight, setCoverageHeight] = useState(`${propCoverageHeight}` ?? "");

    const getCoverageHeight = (): number => {
        // @ts-ignore
        if(!isNaN(coverageHeight)){
            return parseFloat(coverageHeight)
        } else {
            return 0.0
        }
    }

    const handleConfirm = () => {
        // @ts-ignore
        if(isNaN(length1) || length1 <= 0.0) {
            notify("Länge muss eine positive Zahl sein!")
        } else if (isNaN(Number(coverageHeight)) || Number(coverageHeight) < 0.0) {
            notify("Überdeckungshöhe muss eine positive Zahl sein!")
        } else {
            onConfirm(id ?? generateUniqueID(), getLength1(), getCoverageHeight())
        }
    }

    return <FormSkeleton id={id} message={message} onConfirm={() => handleConfirm()} onAbort={onAbort}>
        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Leitungslänge [m]" type="number" placeholder="127.30"
                           value={length1} onChange={(e) => setLength1(e.target.value)}/>
            </Grid>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Überdeckungshöhe [m]" type="number" placeholder="127.30"
                           value={coverageHeight} onChange={(e) => setCoverageHeight(e.target.value)}/>
            </Grid>
        </Grid>
    </FormSkeleton>
}

export const showEditPipeDialog = (message: string,
                                   onConfirm: (id: string, length1: number, coverageHeight: number) => void,
                                   onAbort: () => void,
                                   id: string, length?: number, coverageHeight?: number,) => {

    showPipeDialog(message, onConfirm, onAbort, id, length, coverageHeight)
}



