import {confirmAlert} from "react-confirm-alert";
import React, {useState} from "react";
import {NodeType, OutputNode} from "../models";
import {Button, FormLabel, Grid, TextField, Typography} from "@material-ui/core";
import {notify} from "./Notifications";
import {generateUniqueID} from "web-vitals/dist/modules/lib/generateUniqueID";

export const showNodeOutputDialog = (message: string,
                                     node: OutputNode,
                                     onConfirm: (node: OutputNode) => void,
                                     onAbort: () => void) => {

    showNodeDialog(message, onConfirm, onAbort, NodeType.OUTPUT_NODE, node)
}

export const showNodeDialog = (message: string,
                               onConfirm: (node: OutputNode) => void,
                               onAbort: () => void,
                               type: NodeType, node: OutputNode) => {

    confirmAlert({
        customUI: ({onClose}) =>
            <OutputNodeForm message={message}
                            onConfirm={(newNode: OutputNode) => {
                                onConfirm(newNode)
                                onClose()
                            }} onAbort={() => {
                onAbort()
                onClose()
            }} node={node}
            />
    })
}



export const OutputNodeForm = ({message, onConfirm, onAbort, node}: {
    message: string
    onConfirm: (node: OutputNode) => void,
    onAbort: () => void,
    node: OutputNode
}) => {

    const [label, setLabel] = useState(node.data.label)
    const [thermalEnergyDemand, setThermalEnergyDemand] = useState<string>(`${node.thermalEnergyDemand}`)
    const [pressureLoss, setPressureLoss] = useState<string>(`${node.pressureLoss}`)

    const submitNewNode = () => {
        if (validateInput()) {
            console.log(node)
            const newNode = {
                thermalEnergyDemand: Number(thermalEnergyDemand),
                pressureLoss:  Number(pressureLoss),
                data: {label: label},
                // @ts-ignore
                position: {x: node.position?.x ?? node.xPos, y: node.position?.y ??node.yPos},
                type: NodeType.OUTPUT_NODE,
                id: node.id
            }
            onConfirm(newNode)
        } else {
            notify("Eingaben nicht valide")
        }
    }

    const validateInput = () => {
        const inputsAreNumbers = isNumeric(thermalEnergyDemand) && isNumeric(pressureLoss)
        const pressureLossIsInRange = (Number(pressureLoss) >= 0 && Number(pressureLoss)<=1)

        return inputsAreNumbers && pressureLossIsInRange;
    }

    const isNumeric = (val: string) => {
        //@ts-ignore
        return !isNaN(val)
    }

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
                <FormLabel> Node-ID: {node.id} </FormLabel>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Label" placeholder="OutputLabel"
                           value={label}
                           onChange={(val) =>{
                               setLabel(val.target.value)
                           }}/>
            </Grid>
        </Grid>
        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Warmwasserbedarf [Kwh]" type="number"
                           value={thermalEnergyDemand}
                           onChange={(val) => setThermalEnergyDemand(val.target.value)}
                           placeholder="127.30"/>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Druckverluste" type="number" placeholder="0.30"
                           helperText={"Der Druck muss zwischen 0 und 1 Bar liegen."}
                           value={pressureLoss}
                           onChange={(val) =>{
                               setPressureLoss(val.target.value)
                           }}/>
            </Grid>
        </Grid>


        <Grid container direction="row" item xs={7} spacing={1}>

            <Grid item xs={4}>
                <Button onClick={() => {
                    submitNewNode()
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
