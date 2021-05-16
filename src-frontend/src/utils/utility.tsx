import {Dispatch} from "react";

import {Browser, getBrowser} from "./BrowserChecker";
import {HotWaterGrid, NodeElements, Pipe} from "../models";

export const uploadDropboxInit = (renderUpload: boolean, setRenderUpload: Dispatch<boolean>) => {
    window.addEventListener("dragover", (e) => {
        e.preventDefault();
        setRenderUpload(true);
    }, false);

    window.addEventListener("dragleave", (e) => {
        e.preventDefault();
        if (didLeaveWindow(e) || getBrowser() === Browser.Firefox) {
            setRenderUpload(false);
        }
    });

    const didLeaveWindow = (e: DragEvent) => {
        return e.clientX === 0 || e.clientY === 0 ||
            e.clientX === window.innerWidth || e.clientY === window.innerHeight;
    };
    window.addEventListener("drop", (e) => {
        e.preventDefault();
        setRenderUpload(false);
    }, false);

    window.addEventListener("onmouseleave", () => {
        console.log("MOUSE LEAVING");
    });
}


export const createGrid = (nodes: NodeElements, pipes: Pipe[], temperatureSeries: string): HotWaterGrid => {
    return {...nodes, pipes, temperatureSeries}
}


export const baseUrl = `${window.location.protocol.split(':')[0]}://${window.location.hostname}${window.location.port ? ':' + window.location.port : ''}`
