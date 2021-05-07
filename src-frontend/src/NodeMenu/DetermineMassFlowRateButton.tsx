import React from "react";
import {Button} from "@material-ui/core";
import {baseUrl} from "../utils/utility";
import {HotWaterGrid, MassenstromResponse} from "../models";

export const DetermineMassFlowRateButton = ({grid, onResult}: {grid: HotWaterGrid, onResult: (massenStrom: MassenstromResponse) => void}) => {
    const configuration = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(grid)
    }

    const fetchMassFlowRate = () => {
        fetch(`${baseUrl}/api/grid/maxmassenstrom`, configuration)
            .then(response => {
                return response.json()
            }).then(p => onResult(p))
            .catch(e => {
                return false});
    }

    return <div className={"determine-mass-flow-rate-button"}>
        <Button onClick={fetchMassFlowRate}>Massenstrom bestimmen</Button>
    </div>
}
