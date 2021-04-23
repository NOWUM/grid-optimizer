import {BottomNavigation} from "@material-ui/core";
import React, {useEffect, useState} from "react";

export const VersionNumber = () => {
    const [version, setVersion] = useState()

    useEffect(() => {
        fetchVersionNumber()
    }, [])

    const configuration = {
        method: 'GET',
        headers: {
            "Access-Control-Allow-Origin": "*"
        }
    }

    const fetchVersionNumber = () => {
        return fetch('/api/version', configuration)
            .then(response => {
                return response.json()
            }).then(response => {
                setVersion(response.version)
        })
            .catch(e => {
                console.log(e)});
    }


    return <div style={{position: "absolute", float: "right", right: "10vw", justifySelf: "flex-end"}}>Version {version}</div>
}
