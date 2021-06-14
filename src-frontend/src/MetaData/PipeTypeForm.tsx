import {PipeType} from "../models";
import {Grid, Input} from "@material-ui/core";
import {DEFAULT_GRID_SPACING} from "../utils/defaults";
import React, {useState} from "react";
import {AddCircleOutline, DeleteForever} from "@material-ui/icons";
import {isPositiveNumber} from "../utils/utility";
import {notify} from "../ReactFlow/Overlays/Notifications";

export const PipeTypeForm = ({pipeTypes, setPipeTypes}: { pipeTypes: PipeType[], setPipeTypes: (pt: PipeType[]) => void }) => {
    const [formDiameter, setFormDiameter] = useState("");
    const [formCost, setFormCost] = useState("")

    const deletePipeType = (index: number) => {
        setPipeTypes(pipeTypes.filter((p, i) => i !== index))
    }

    const addPipeType = () => {
        if (isPositiveNumber(formDiameter) && isPositiveNumber(formCost)) {
            const newPipeTypes = [...pipeTypes]
            newPipeTypes.push({diameter: parseFloat(formDiameter), costPerMeter: parseFloat(formCost)});
            setPipeTypes(newPipeTypes)
            resetForm()
        } else {
            notify("Eingaben müssen positive Zahlen sein.")
        }
    }

    const resetForm = () => {
        setFormDiameter("")
        setFormCost("")
    }

    return <>
        <Grid container
              direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={5}>
                Durchmesser in Millimeter
            </Grid>
            <Grid item xs={5}>
                Kosten pro Meter
            </Grid>
            <Grid item xs={2}>

            </Grid>
        </Grid>

        {pipeTypes.map((p, index) => <Grid container
                                           direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={5}>
                {p.diameter}
            </Grid>
            <Grid item xs={5}>
                {p.costPerMeter}€
            </Grid>
            <Grid item xs={2}>
                <DeleteForever style={{fill: "red"}} onClick={() => deletePipeType(index)}/>
            </Grid>
        </Grid>)}

        <Grid container
              direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={5}>
                <Input type={"number"} value={formDiameter} placeholder={"Durchschnitt in Meter"}
                       onChange={(e) => setFormDiameter(e.target.value)}/>
            </Grid>
            <Grid item xs={5}>
                <Input type={"number"} value={formCost} placeholder={"Kosten pro Meter"}
                       onChange={(e) => setFormCost(e.target.value)}/>
            </Grid>
            <Grid item xs={2}>
                <AddCircleOutline style={{fill: "green"}} onClick={() => addPipeType()}/>
            </Grid>
        </Grid>
    </>
}
