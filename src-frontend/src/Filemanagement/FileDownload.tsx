import {HotWaterGrid} from "../models";
import {CloudDownload, CloudUpload} from "@material-ui/icons";
import "./file-download.css"
import {notify} from "../Overlays/Notifications";
import React from "react";


export const FileDownload = ({grid, setRenderUpload}:
                                 {grid: HotWaterGrid, setRenderUpload: (val: boolean) => {}}) => {

    const isGridEmpty = () => {
        return (grid.pipes.length === 0 && grid.inputNodes.length === 0 && grid.intermediateNodes.length === 0 && grid.outputNodes.length === 0 )
    }

    const handleClick = () => {
        if(!isGridEmpty()) {
            handleDownloadAsFile(JSON.stringify(grid), getFileName());
        } else {
            notify("ALARM. Keine Daten vorhanden. ALAAAARM!")
        }
    };

    const getFileName = (): string => {
        const date =  new Date();
        const dd = String(date.getDate()).padStart(2, '0');
        const mm = String(date.getMonth() + 1).padStart(2, '0');
        const yyyy = date.getFullYear();

        const dateStr = mm + '/' + dd + '/' + yyyy;
        return `Grid-${dateStr}`;
    };

    const handleDownloadAsFile = (text: string, fileName: string) => {
        const element = document.createElement("a");
        const file = new Blob([text], {type: 'application/json'});
        element.href = URL.createObjectURL(file);
        element.download = `${fileName}.json`;
        document.body.appendChild(element); // Required for this to work in FireFox
        element.click();
    };

    return <div className={"file-download-container"} id={"#download"}>
        <div onClick={() => {handleClick(); }}>
            <CloudDownload style={{fontSize: "3vw"}} />

            <CloudUpload style={{fontSize: "3vw"}} onClick={() => setRenderUpload(true)} />
        </div>
    </div>;
};
