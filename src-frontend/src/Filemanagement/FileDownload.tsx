import {HotWaterGrid} from "../models/models";
import {CloudDownload, CloudUpload} from "@material-ui/icons";
import "./file-download.css"
import {notify} from "../ReactFlow/Overlays/Notifications";
import React from "react";


export const handleDownloadAsFile = (text: string | Blob, fileName: string, dataFormat: string) => {
    const element = document.createElement("a");
    const file = new Blob([text], {type: 'application/json'});
    element.href = URL.createObjectURL(file);
    element.download = `${fileName}.${dataFormat}`;
    document.body.appendChild(element); // Required for this to work in FireFox
    element.click();
};

export const getFileName = (): string => {
    const date =  new Date();
    const dd = String(date.getDate()).padStart(2, '0');
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const yyyy = date.getFullYear();

    const dateStr = mm + '/' + dd + '/' + yyyy;
    return `Grid-${dateStr}`;
};

export const FileDownload = ({grid, setRenderUpload}:
                                 {grid: HotWaterGrid, setRenderUpload: (val: boolean) => {}}) => {

    const isGridEmpty = () => {
        return (grid.pipes.length === 0 && grid.inputNodes.length === 0 && grid.intermediateNodes.length === 0 && grid.outputNodes.length === 0 )
    }

    const handleClick = () => {
        if(!isGridEmpty()) {
            handleDownloadAsFile(JSON.stringify(grid), getFileName(), "json");
        } else {
            notify("ALARM. Keine Daten vorhanden. ALAAAARM!")
        }
    };



    return <div className={"file-download-container"} id={"#download"}>
        <div >
            <CloudDownload style={{fontSize: "3vw"}} onClick={handleClick}/>

            <CloudUpload style={{fontSize: "3vw"}} onClick={() => setRenderUpload(true)} />
        </div>
    </div>;
};
