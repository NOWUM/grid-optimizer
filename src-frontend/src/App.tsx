import React, {useEffect, useState} from 'react';
import './App.css';
import {FlowContainer} from "./FlowContainer";
import {FileUpload} from "./Filemanagement/FileUpload";
import {uploadDropboxInit} from "./utils/utility";
import {HotWaterGrid} from "./models";



function App() {

    const [renderUpload, setRenderUpload] = useState<boolean>(false);
    const [grid, setGrid] = useState<HotWaterGrid>({nodes:[], pipes: []})

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
                <FlowContainer data={[...grid.nodes, ...grid.pipes]}/>
            </div>

        </div>
    );
}

export default App;
