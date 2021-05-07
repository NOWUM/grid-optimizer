import {InputBase, makeStyles, MenuItem, Select, withStyles} from "@material-ui/core";
import React, {useState} from "react";

const BootstrapInput = withStyles((theme) => ({
    root: {
        'label + &': {
            marginTop: theme.spacing(3),
        },
    },
    input: {
        borderRadius: 4,
        position: 'relative',
        backgroundColor: theme.palette.background.paper,
        border: '1px solid #ced4da',
        fontSize: 16,
        padding: '10px 26px 10px 12px',
        transition: theme.transitions.create(['border-color', 'box-shadow']),
        // Use the system font instead of the default Roboto font.
        fontFamily: [
            '-apple-system',
            'BlinkMacSystemFont',
            '"Segoe UI"',
            'Roboto',
            '"Helvetica Neue"',
            'Arial',
            'sans-serif',
            '"Apple Color Emoji"',
            '"Segoe UI Emoji"',
            '"Segoe UI Symbol"',
        ].join(','),
        '&:focus': {
            borderRadius: 4,
            borderColor: '#80bdff',
            boxShadow: '0 0 0 0.2rem rgba(0,123,255,.25)',
        },
    },
}))(InputBase);

const useStyles = makeStyles((theme) => ({
    margin: {
        margin: theme.spacing(1),
    },
}));

export const CustomSelect = ({value, options, onValueChange}: {value: string, options: string[],
    onValueChange: (val: string) => void}) => {

    const [open, setOpen] = useState(false)




    return <Select
        label="Standard Lastprofile"
        id='outlined-age-native-simple'
        open={open}
        onClose={() => setOpen(false)}
        onOpen={() => setOpen(true)}
        value={value}
        onChange={(e) => onValueChange(e.target.value as string)}
        input={<BootstrapInput/>}
    >
        <MenuItem value="">
            <em>Please select</em>
        </MenuItem>

        {options.map((option) => {
            return <MenuItem value={option}>{option}</MenuItem>
        })}
    </Select>
}
