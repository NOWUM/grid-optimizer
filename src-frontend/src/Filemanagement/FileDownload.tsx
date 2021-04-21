import {HotWaterGrid} from "../models";
import {CloudDownload} from "@material-ui/icons";
import "./file-download.css"
import {notify} from "../Overlays/Notifications";


export const FileDownload = ({grid}: {grid: HotWaterGrid}) => {

    const isGridEmpty = () => {
        return (grid.pipes && grid.inputNodes && grid.intermediateNodes && grid.outputNodes )
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

    return <div className={"file-download-container"}>
        <div onClick={() => {handleClick(); }}>
            <CloudDownload style={{fontSize: "3vw"}} />
        </div>
    </div>;
};
