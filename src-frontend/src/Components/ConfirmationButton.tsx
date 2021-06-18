import React, {useEffect} from "react";
import {Button} from "@material-ui/core";

export enum KeyboardKey {
    ENTER = "Enter",
    ESC = "Escape",
    DEL = "Delete"
}

export const handleKeyDown = (e: KeyboardEvent, key: KeyboardKey, onKeyDown: () => void) => {
    if (e.key === KeyboardKey.ENTER) {
        onKeyDown()
    }
}


export const ConfirmationButton = ({label, onConfirm}: { label: string, onConfirm: () => void }) => {

    useEffect(() => {
        document.addEventListener('keydown',
            (e) => handleKeyDown(e, KeyboardKey.ENTER, onConfirm), false);
        return () => {
            document.removeEventListener('keydown',
                (e) => handleKeyDown(e, KeyboardKey.ENTER, onConfirm), false);
        }
    }, [])

    return <Button onClick={onConfirm}>
        {label}
    </Button>
}
