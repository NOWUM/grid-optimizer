import React from "react";
import {Button} from "@material-ui/core";
import {baseUrl} from "../utils/utility";
import {HotWaterGrid} from "../models";

export const DetermineMassFlowRateButton = ({grid}: {grid: HotWaterGrid}) => {
    const configuration = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(grid)
    }

    const fetchMassFlowRate = () => {
        fetch(`${baseUrl}/api/grid/massenstrom`, configuration)
            .then(response => {
                return response.json()
            }).then(p => console.log(p))
            .catch(e => {
                return false});
    }





    return <div className={"determine-mass-flow-rate-button"}>
        <Button onClick={fetchMassFlowRate}>Massenstrom bestimmen</Button>
    </div>
}
