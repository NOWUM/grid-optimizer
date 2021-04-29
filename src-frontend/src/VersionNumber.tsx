import React, {useEffect, useState} from "react";
import {baseUrl} from "./utils/utility";

export const VersionNumber = () => {
    const [version, setVersion] = useState()

    useEffect(() => {
        fetchVersionNumber()
    }, [])

    const configuration = {
        method: 'GET'
    }

    const fetchVersionNumber = () => {
        fetch(`${baseUrl}/api/version`, configuration)
            .then(response => {
                return response.json()
            }).then(response => {
                setVersion(response.version)
        })
            .catch(e => {
                console.log(e)});
    }

    return <div style={{position: "absolute", float: "right", right: "2vw", top: "15vh", justifySelf: "flex-end"}}>
        Version {version}</div>
}
