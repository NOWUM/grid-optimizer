import React, {useEffect} from "react";
import {handleKeyDown, KeyboardKey} from "./ConfirmationButton";
import {Button} from "@material-ui/core";

export const DeleteButton = ({label, onDelete}: {label: string, onDelete: () => void}) => {

    useEffect(() => {
        document.addEventListener('keydown',
            (e) => handleKeyDown(e, KeyboardKey.DEL, onDelete), false);
        return () => {
            document.removeEventListener('keydown',
                (e) => handleKeyDown(e, KeyboardKey.ESC, onDelete), false);
            console.log("Remove Esc")
        }
    }, [])

    return <Button onClick={onDelete}>
        {label}
    </Button>
}
