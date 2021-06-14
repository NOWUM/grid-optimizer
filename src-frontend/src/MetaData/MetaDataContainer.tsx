import React, {Dispatch, SetStateAction} from "react";
import {
    Divider,
    Grid,
    GridDirection,
    GridJustification,
    GridProps,
    InputLabel,
    TextField,
    Typography
} from "@material-ui/core";
import {TemperatureDropdown} from "./TemperatureDropdown";
import {OptimizationMetadata, PipeType} from "../models";
import {DEFAULT_GRID_SPACING} from "../utils/defaults";
import {GridItemsAlignment} from "@material-ui/core/Grid/Grid";
import {PipeTypeForm} from "./PipeTypeForm";

interface Properties {
    temperatureKey: string,
    setTemperatureKey: Dispatch<SetStateAction<string>>
    optimizationMetadata: OptimizationMetadata,
    setOptimizationMetadata: (om: OptimizationMetadata) => void,
}

export const MetaDataContainer = ({temperatureKey, setTemperatureKey, optimizationMetadata, setOptimizationMetadata}: Properties) => {

    const {
        gridOperatingCostTemplate,
        pumpInvestCostTemplate,
        heatGenerationCost,
        wacc,
        electricityCost,
        electricalEfficiency,
        hydraulicEfficiency,
        lifespanOfGrid, // Jahre
        lifespanOfPump,
        yearsOfOperation,
        pipeTypes
    } = optimizationMetadata;


    const dispatchChange = (val: string, prop: string) => {
        const opt = {...optimizationMetadata};
        let valToInsert: (string|number) = val;
        if (isNumberProp(prop)) {
            if(!Number.isNaN(val) && val !== "") {
                valToInsert = getAsNumber(val);
                // @ts-ignore
                opt[prop] = valToInsert;
                setOptimizationMetadata(opt)
            }
        } else {
            // @ts-ignore
            opt[prop] = valToInsert;
            setOptimizationMetadata(opt)
        }
    }

    const isNumberProp = (prop: string) => {
        const numberProps = [
            "heatGenerationCost",
            "lifespanOfResources",
            "wacc",
            "electricityCost",
            "electricalEfficiency",
            "hydraulicEfficiency",
            "yearsOfOperation", "lifespanOfGrid",
            "lifespanOfPump",]
        return numberProps.includes(prop)
    }

    const getAsNumber = (val: string): number => {
        return Number.parseFloat(val);
    }

    const setPipeTypes = (pt: PipeType[]) => {
        optimizationMetadata.pipeTypes = pt;
        setOptimizationMetadata({...optimizationMetadata})
    }

    const defaultGridSkeletonProps: GridProps = {
        container: true,
        direction: ("row" as GridDirection),
        justify: ("center" as GridJustification),
        alignItems: ("center" as GridItemsAlignment),
        spacing: DEFAULT_GRID_SPACING,
    }

    return <>
        <Grid {...defaultGridSkeletonProps}>
            <Grid container
                  direction="row"
                  alignContent="center"
                  justify="center" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <Grid item xs={12}>
                    <Typography className={"header-form"} color="textSecondary" gutterBottom>
                        Hier kannst du gegebenenfalls die Meta Daten anpassen
                    </Typography>
                </Grid>
            </Grid>


            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <Grid item xs={6}>
                    <InputLabel>Netzoperationskosten Template (f(Netzinvestitionskosten) = [€/a])</InputLabel>
                </Grid>
                <Grid item xs={6}>
                    <TextField id="outlined-basic" type="text" variant="outlined" placeholder="3*cost"
                               value={gridOperatingCostTemplate}
                               onChange={(e) => dispatchChange(e.target.value, "gridOperatingCostTemplate")}/>

                </Grid>
            </Grid>

            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <Grid item xs={6}>
                    <InputLabel>Pumpeninvestitionskosten Template (f(Leistung) = [€/kW])</InputLabel>
                </Grid>
                <Grid item xs={6}>
                    <TextField id="outlined-basic" type="text" variant="outlined" placeholder="3*d"
                               value={pumpInvestCostTemplate}
                               onChange={(e) => dispatchChange(e.target.value, "pumpInvestCostTemplate")}/>

                </Grid>
            </Grid>

            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <Grid item xs={6}>
                    <InputLabel>Heizkosten [€/kWh])</InputLabel>
                </Grid>
                <Grid item xs={6}>
                    <TextField id="outlined-basic" type="text" variant="outlined" placeholder="0.12"
                               value={heatGenerationCost}
                               onChange={(e) => dispatchChange(e.target.value, "heatGenerationCost")}/>

                </Grid>
            </Grid>

            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <Grid item xs={6}>
                    <InputLabel>Auslegungszeit des Netzes[a]</InputLabel>
                </Grid>
                <Grid item xs={6}>
                    <TextField id="outlined-basic" type="text" variant="outlined" placeholder="25"
                               value={lifespanOfGrid}
                               onChange={(e) => dispatchChange(e.target.value, "lifespanOfGrid")}/>

                </Grid>
            </Grid>
            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <Grid item xs={6}>
                    <InputLabel>Auslegungszeit der Pumpe[a]</InputLabel>
                </Grid>
                <Grid item xs={6}>
                    <TextField id="outlined-basic" type="text" variant="outlined" placeholder="25"
                               value={lifespanOfPump}
                               onChange={(e) => dispatchChange(e.target.value, "lifespanOfPump")}/>

                </Grid>
            </Grid>

            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <Grid item xs={6}>
                    <InputLabel>Auslegungszeit der Anlage[a]</InputLabel>
                </Grid>
                <Grid item xs={6}>
                    <TextField id="outlined-basic" type="text" variant="outlined" placeholder="25"
                               value={yearsOfOperation}
                               onChange={(e) => dispatchChange(e.target.value, "yearsOfOperation")}/>

            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
            <Grid item xs={6}>
                <InputLabel>Weighted Average Cost of Capital [%]</InputLabel>
            </Grid>
            <Grid item xs={6}>
                <TextField id="outlined-basic" type="text" variant="outlined" placeholder="15"
                           value={wacc}
                           onChange={(e) => dispatchChange(e.target.value, "wacc")}/>

            </Grid>
        </Grid>
        </Grid>
        <Divider style={{margin: "10px"}}/>
        <Grid {...defaultGridSkeletonProps}>
            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <h3>Pumpen</h3>
            </Grid>

            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <Grid item xs={6}>
                    <InputLabel>Elektrizitätskosten Pumpstation [ct/kWh]</InputLabel>
                </Grid>
                <Grid item xs={6}>
                    <TextField id="outlined-basic" type="text" variant="outlined" placeholder="15"
                               value={electricityCost}
                               onChange={(e) => dispatchChange(e.target.value, "electricityCost")}/>

                </Grid>
            </Grid>
            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <Grid item xs={6}>
                    <InputLabel>Elektrische Effizienz (Pumpe)</InputLabel>
                </Grid>
                <Grid item xs={6}>
                    <TextField id="outlined-basic" type="text" variant="outlined" placeholder="15"
                               value={electricalEfficiency}
                               onChange={(e) => dispatchChange(e.target.value, "electricalEfficiency")}/>

                </Grid>
            </Grid>
            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <Grid item xs={6}>
                    <InputLabel>Hydraulische Effizienz (Pumpe)</InputLabel>
                </Grid>
                <Grid item xs={6}>
                    <TextField id="outlined-basic" type="text" variant="outlined" placeholder="15"
                               value={hydraulicEfficiency}
                               onChange={(e) => dispatchChange(e.target.value, "hydraulicEfficiency")}/>

                </Grid>
            </Grid>

            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <Grid item xs={6}>
                    <InputLabel>Temperaturreihe</InputLabel>
                </Grid>
                <Grid item xs={6}>
                    <TemperatureDropdown temperatureKey={temperatureKey} setTemperatureKey={setTemperatureKey}/>
                </Grid>
            </Grid>

        </Grid>

        <Divider style={{margin: "10px", height: "1.5px"}} />

        <Grid {...defaultGridSkeletonProps} style={{marginBottom: "10vh"}}>
            <Grid container
                  direction="row" item xs={7} spacing={DEFAULT_GRID_SPACING}>
                <h3>Rohrtypen</h3>
            </Grid>
            <PipeTypeForm pipeTypes={pipeTypes} setPipeTypes={setPipeTypes}/>
        </Grid>

    </>
}

