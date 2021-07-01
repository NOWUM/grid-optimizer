import React from "react";
import "./optimization-status.css"
import greenSphere from "../icons/Green_sphere.svg"
import redSphere from "../icons/Red_Sphere.svg"
import orangeSphere from "../icons/Orange_sphere.svg"

export const OptimizationStatusIndicator = ({status}: { status?: boolean }) => {
    const getStatusMessage = () => {
        switch (status) {
            case undefined:
                return <>
                    <span><img alt={"redSphere"} className={"sphere"} src={redSphere}/></span>
                    Optimierung nicht gestartet
                </>
            case true:
                return <>
                    <span><img alt={"greenSphere"} className={"sphere"} src={greenSphere}/></span>
                    Optimierung abgeschlossen
                </>
            case false:
                return <>
                    <span><img alt={"orangeSphere"} className={"sphere"} src={orangeSphere}/></span>
                    Optimierung läuft
                </>
        }
        return <>error</>
    }

    if (status === undefined) {
        return <></>
    } else {
        return <div>
            {getStatusMessage()}
        </div>
    }
}
