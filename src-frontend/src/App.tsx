import React, {useEffect, useState} from 'react';
import './App.css';
import {FlowContainer} from "./FlowContainer";
import {FileUpload} from "./Filemanagement/FileUpload";
import {uploadDropboxInit} from "./utils/utility";
import {HotWaterGrid, NodeElements, Pipe} from "./models";
import {FileDownload} from "./Filemanagement/FileDownload";
import Notifications from "./Overlays/Notifications";
import {Elements} from "react-flow-renderer";
import {UserTour} from "./UserTour/UserTour";
import {AppBar, Tab} from "@material-ui/core";
import {TabContext, TabList, TabPanel} from "@material-ui/lab";
import {MetaDataContainer} from "./MetaData/MetaDataContainer";

const pipe = require("./pipe.svg")


function App() {

    const [renderUpload, setRenderUpload] = useState<boolean>(false);
    const [tabVal, setTabVal] = useState("1")

    const [nodeElements, setNodeElements] = useState<NodeElements>({
        inputNodes: [],
        outputNodes: [],
        intermediateNodes: []
    });
    const [pipes, setPipes] = useState<Elements<Pipe>>([])

    useEffect(() => {
        uploadDropboxInit(renderUpload, setRenderUpload)
    }, []);

    const getNodeElements = (hwg: HotWaterGrid): NodeElements => {
        return {inputNodes: hwg.inputNodes, intermediateNodes: hwg.intermediateNodes, outputNodes: hwg.outputNodes}
    }

    const insertGrid = (hwg: HotWaterGrid) => {
        setNodeElements(getNodeElements(hwg))
        setPipes(hwg.pipes)
    }

    const clearGrid = () => {
        setNodeElements({intermediateNodes: [], outputNodes: [], inputNodes: []})
        setPipes([])
    }

    return (


        <div className="App">
            <TabContext value={tabVal}>
                {// @ts-ignore
                }<AppBar position="static">
                <h1 style={{userSelect: "none"}}>Pipify</h1>
                <TabList onChange={(e, val) => setTabVal(val)} aria-label="simple tabs example">

                    <Tab label="Editor" value="1"/>

                    <Tab label="Meta Daten" value="2"/>
                </TabList>
            </AppBar>
                <TabPanel value="1">
                    <div className="react-flow-container">
                        <FlowContainer pipes={pipes} setPipes={setPipes}
                                       nodeElements={nodeElements} setNodeElements={setNodeElements}
                        />
                    </div>
                </TabPanel>
                <TabPanel value="2"><MetaDataContainer/></TabPanel>
            </TabContext>
            {renderUpload ?
                <FileUpload loadGrid={(hwg) => {
                    console.log(hwg)
                    insertGrid(hwg)
                }}/> : <></>
            }
            {/* @ts-ignore*/}
            <FileDownload grid={{...nodeElements, pipes}}/>
            <Notifications/>
            <UserTour endTest={clearGrid} startTest={insertGrid}/>
        </div>
    );
}

export default App;
