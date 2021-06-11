import {Button} from "@material-ui/core";
import React from "react";
import {notify} from "../Overlays/Notifications";
import "./optimization-button.css";

export const OptimizeButton = () => {

    const optimize = () => {
        notify("Optimization to be implemented")
    }

    return <div className={"optimize-button"}>
        <Button onClick={optimize}>Optimiere Netz</Button>
    </div>
}
