import React, {useEffect} from "react";
import {Button} from "@material-ui/core";
import {handleKeyDown, KeyboardKey} from "./ConfirmationButton";

export const AbortButton = ({label, onAbort}: {label: string, onAbort: () => void}) => {

    // useEffect(() => {
    //     document.addEventListener('keydown',
    //         (e) => handleKeyDown(e, KeyboardKey.ESC, onAbort), false);
    //     return () => {
    //         document.removeEventListener('keydown',
    //             (e) => handleKeyDown(e, KeyboardKey.ESC, onAbort), false);
    //     }
    // }, [])

    return <Button onClick={onAbort}>
        {label}
    </Button>
}
