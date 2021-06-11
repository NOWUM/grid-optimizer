import {CustomSelect} from "./CustomSelect";
import React, {useEffect, useState} from "react";
import {notify} from "../ReactFlow/Overlays/Notifications";
import {fetchLoadProfileOptions} from "../ReactFlow/Overlays/NodeContextOverlay";

export const LoadProfileSelect = ({value, onValueChange}: {value: string, onValueChange: (val: string) => void}) => {

    const [loadProfileOptions, setLoadProfileOption] = useState<string[]>([])

    useEffect(() => {
        fetchLoadProfileOptions().then(options => {
            if(options) {
                setLoadProfileOption(options)
            } else {
                notify("Load Profiles could not be fetched.")
            }
        })
    }, [])


    return <CustomSelect value={value} options={loadProfileOptions} onValueChange={onValueChange} />
}
