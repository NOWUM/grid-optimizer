import React, {Dispatch, SetStateAction, useEffect, useState} from "react";
import {baseUrl} from "../utils/utility";
import {defaultGetConfiguration} from "../ReactFlow/Overlays/NodeContextOverlay";
import {CustomSelect} from "../Components/CustomSelect";


interface Properties {
    temperatureKey: string,
    setTemperatureKey: Dispatch<SetStateAction<string>>
}

export const TemperatureDropdown = ({temperatureKey, setTemperatureKey}: Properties) => {
    const [temperatureKeys, setTemperatureKeys] = useState([])

    useEffect(() => fetchTemperatureKeys(), [])

    const fetchTemperatureKeys = (): void => {
        fetch(`${baseUrl}/api/temperature/keys`, defaultGetConfiguration)
            .then(response => {
                return response.json()
            }).then(setTemperatureKeys)
            .catch(e => {
                console.error(e)
            });
    }

    return <>
        <CustomSelect value={temperatureKey} options={temperatureKeys} onValueChange={setTemperatureKey} />
    </>
}
