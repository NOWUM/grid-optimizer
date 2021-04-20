import React, {useEffect, useState} from 'react';
import './App.css';
import {FlowContainer} from "./FlowContainer";
import {FileUpload} from "./Filemanagement/FileUpload";
import {uploadDropboxInit} from "./utils/utility";



function App() {

    const [renderUpload, setRenderUpload] = useState<boolean>(false);

    useEffect(() => {
        uploadDropboxInit(renderUpload, setRenderUpload)

    }, []);


    return (
        <div className="App">
            {renderUpload ?
                <FileUpload loadGrid={(hwg) => {
                    console.log(hwg)
                    setRenderUpload(false)
                }}/>: <></>
            }
            <div className="react-flow-container">
                <FlowContainer/>
            </div>

        </div>
    );
}

export default App;
