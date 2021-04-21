import React, {useEffect, useState} from 'react';
import './App.css';
import {FlowContainer} from "./FlowContainer";
import {FileUpload} from "./Filemanagement/FileUpload";
import {uploadDropboxInit} from "./utils/utility";
import {HotWaterGrid} from "./models";
import {FileDownload} from "./Filemanagement/FileDownload";
import Notifications from "./Overlays/Notifications";



function App() {

    const [renderUpload, setRenderUpload] = useState<boolean>(false);
    const [grid, setGrid] = useState<HotWaterGrid>({inputNodes: [], outputNodes: [], intermediateNodes: [], pipes: []})

    useEffect(() => {
        uploadDropboxInit(renderUpload, setRenderUpload)
    }, []);


    return (
        <div className="App">
            {renderUpload ?
                <FileUpload loadGrid={(hwg) => {
                    console.log(hwg)
                    setGrid(hwg)
                    // setRenderUpload(false)
                }}/>: <></>
            }
            <div className="react-flow-container">
                <FlowContainer data={grid}/>
            </div>
            <FileDownload grid={grid} />
            <Notifications />
        </div>
    );
}

export default App;
