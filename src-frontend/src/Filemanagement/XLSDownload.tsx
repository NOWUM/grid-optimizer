import {Button} from "@material-ui/core";
import {notify} from "../ReactFlow/Overlays/Notifications";
import {getFileName, handleDownloadAsFile} from "./FileDownload";
import React from "react";
import {baseUrl} from "../utils/utility";
import {getConfiguration} from "../ReactFlow/OverlayButtons/OptimizeButton";
import {ResultCode} from "../ReactFlow/FlowContainer";

export const XLSDownload = ({optId}: {optId: string}) => {

    const handleClick = () => {
        fetchXLS()
    };

    const fetchXLS = () => {
            fetch(`${baseUrl}/api/optimize/${optId}/download`, getConfiguration)
                .then(response => {
                    if (response.status !== ResultCode.OK) {
                        response.text().then(text => {
                            if (text) {
                                notify(text)
                            } else {
                                notify('Unbekannter Fehler beim Aufruf der Optimierungsergebnisse.')
                            }
                        });
                        throw 'Status code not good.';
                    }
                    return response.blob();
                }).then((res) => handleDownloadAsFile(res, "Report", "xls"))
                .catch(e => {
                    console.error(e)
                })
    }

    return <Button onClick={handleClick}>Download XLS</Button>
}
