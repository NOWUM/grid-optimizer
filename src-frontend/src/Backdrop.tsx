import React from "react";
import {CircularProgress, makeStyles} from "@material-ui/core";
import MUIBackdrop from '@material-ui/core/Backdrop';

const useStyles = makeStyles((theme) => ({
    backdrop: {
        zIndex: 9999,
        color: '#fff',
    },
}));

export default function Backdrop({open}: {open: boolean}) {
    const classes = useStyles();

    return (
        <div>
            <MUIBackdrop className={classes.backdrop} open={open}>
                <CircularProgress color="inherit" />
            </MUIBackdrop>
        </div>
    );
}
