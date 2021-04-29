import {Button, FormLabel, Grid, Typography} from "@material-ui/core";
import React, {FunctionComponent} from "react";

interface SkeletonInterface {
    id: string, onConfirm: () => void, onAbort: () => void, message: string, children: any
}

export const FormSkeleton: FunctionComponent<SkeletonInterface> = (props) => {
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
                    {props.message}
                </Typography>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <FormLabel> ID: {props.id} </FormLabel>
            </Grid>
        </Grid>

        {props.children}

        <Grid container direction="row" item xs={7} spacing={1}>

            <Grid item xs={4}>
                <Button onClick={props.onConfirm}>
                    Bestätigen
                </Button>
            </Grid>
            <Grid item xs={6}>
                <Button onClick={props.onAbort}>
                    Abbruch
                </Button>
            </Grid>
        </Grid>
    </Grid>
}
