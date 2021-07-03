import {PipeType} from "../models/models";
import {Grid, Input} from "@material-ui/core";
import {DEFAULT_GRID_SPACING} from "../utils/defaults";
import React, {useState} from "react";
import {AddCircleOutline, DeleteForever} from "@material-ui/icons";
import {isPositiveNumber} from "../utils/utility";
import {notify} from "../ReactFlow/Overlays/Notifications";

export const PipeTypeForm = ({pipeTypes, setPipeTypes}: { pipeTypes: PipeType[], setPipeTypes: (pt: PipeType[]) => void }) => {
    const [formDiameter, setFormDiameter] = useState("");
    const [formCost, setFormCost] = useState("");
    const [formIsolationThickness, setFormIsolationThickness] = useState("");
    const [formDistanceBetweenPipes, setFormDistanceBetweenPipes] = useState("");

    const deletePipeType = (index: number) => {
        setPipeTypes(pipeTypes.filter((p, i) => i !== index))
    }

    const addPipeType = () => {
        if (isPositiveNumber(formDiameter) && isPositiveNumber(formCost) && isPositiveNumber(formIsolationThickness) && isPositiveNumber(formDistanceBetweenPipes)) {
            const newPipeTypes = [...pipeTypes]
            newPipeTypes.push(
                {
                    diameter: parseFloat(formDiameter),
                    costPerMeter: parseFloat(formCost),
                    isolationThickness: parseFloat(formIsolationThickness),
                    distanceBetweenPipes: parseFloat(formDistanceBetweenPipes)
                });
            setPipeTypes(newPipeTypes)
            resetForm()
        } else {
            notify("Eingaben müssen positive Zahlen sein.")
        }
    }

    const resetForm = () => {
        setFormDiameter("")
        setFormCost("")
        setFormDistanceBetweenPipes("")
        setFormIsolationThickness("")
    }

    return <>
        <Grid container
              direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={2}>
                Durchmesser [mm]
            </Grid>
            <Grid item xs={2}>
                Kosten [€/m]
            </Grid>
            <Grid item xs={2}>
                Isolationsdicke [mm]
            </Grid>
            <Grid item xs={2}>
                Abstand Vorlauf/Rücklauf [mm]
            </Grid>
            <Grid item xs={1}>

            </Grid>
        </Grid>

        {pipeTypes.map((p, index) => <Grid container
                                           direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={2}>
                {p.diameter} mm
            </Grid>
            <Grid item xs={2}>
                {p.costPerMeter} €
            </Grid>
            <Grid item xs={2}>
                {p.isolationThickness} mm
            </Grid>
            <Grid item xs={2}>
                {p.distanceBetweenPipes} mm
            </Grid>
            <Grid item xs={1}>
                <DeleteForever style={{fill: "red"}} onClick={() => deletePipeType(index)}/>
            </Grid>
        </Grid>)}

        <Grid container
              direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={2}>
                <Input type={"number"} value={formDiameter} placeholder={"Durchmesser [mm]"}
                       onChange={(e) => setFormDiameter(e.target.value)}/>
            </Grid>
            <Grid item xs={2}>
                <Input type={"number"} value={formCost} placeholder={"Kosten [€/m]"}
                       onChange={(e) => setFormCost(e.target.value)}/>
            </Grid>

            <Grid item xs={2}>
                <Input type={"number"} value={formIsolationThickness} placeholder={"Isolationsdicke [mm]"}
                       onChange={(e) => setFormCost(e.target.value)}/>
            </Grid>

            <Grid item xs={2}>
                <Input type={"number"} value={formDistanceBetweenPipes} placeholder={"Abstand Leitungen [mm]"}
                       onChange={(e) => setFormCost(e.target.value)}/>
            </Grid>
            <Grid item xs={1}>
                <AddCircleOutline style={{fill: "green"}} onClick={() => addPipeType()}/>
            </Grid>
        </Grid>
    </>
}
