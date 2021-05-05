import React, {useEffect, useState} from 'react';
import './App.css';
import {FlowContainer} from "./FlowContainer";
import {FileUpload} from "./Filemanagement/FileUpload";
import {uploadDropboxInit} from "./utils/utility";
import {BaseNode, HotWaterGrid, InputNode, IntermediateNode, NodeElements, NodeType, OutputNode, Pipe} from "./models";
import {FileDownload} from "./Filemanagement/FileDownload";
import {Elements} from "react-flow-renderer";
import {UserTour} from "./UserTour/UserTour";
import {AppBar, Tab} from "@material-ui/core";
import {TabContext, TabList, TabPanel} from "@material-ui/lab";
import {MetaDataContainer} from "./MetaData/MetaDataContainer";
import {getPipe} from "./pipe";
import {VersionNumber} from "./VersionNumber";
import {NodeMenuSpawnerContainer} from "./NodeMenu/NodeMenuSpawnerContainer";
import Notifications from "./Overlays/Notifications";
import {OptimizationResults} from "./OptimizationResults";
import {DetermineMassFlowRateButton} from "./NodeMenu/DetermineMassFlowRateButton";

function App() {

    const [renderUpload, setRenderUpload] = useState<boolean>(false);
    const [tabVal, setTabVal] = useState("1")

    const [nodeElements, setNodeElements] = useState<NodeElements>({
        inputNodes: [],
        intermediateNodes: [],
        outputNodes: []
    });
    const [pipes, setPipes] = useState<Elements<Pipe>>([])

    useEffect(() => {
        uploadDropboxInit(renderUpload, setRenderUpload)
    }, []);

    useEffect(() => {
        console.log(nodeElements.outputNodes)
    }, [nodeElements])

    const getNodeElements = (hwg: HotWaterGrid): NodeElements => {
        return {inputNodes: hwg.inputNodes, intermediateNodes: hwg.intermediateNodes, outputNodes: hwg.outputNodes}
    }

    const insertGrid = (hwg: HotWaterGrid) => {
        setNodeElements(getNodeElements(hwg))
        setPipes(hwg.pipes)
    }

    const clearGrid = () => {
        setNodeElements({ inputNodes: [], intermediateNodes: [], outputNodes: [],})
        setPipes([])
    }

    const handleNewNode = (newNode: BaseNode) => {
        const newNodeElements = {...nodeElements};
        switch (newNode.type) {
            case NodeType.INPUT_NODE: newNodeElements.inputNodes.push(newNode as InputNode)
                break;
            case NodeType.INTERMEDIATE_NODE: newNodeElements.intermediateNodes.push(newNode as IntermediateNode)
                break;
            case NodeType.OUTPUT_NODE: newNodeElements.outputNodes.push(newNode as OutputNode)
                break;
            default: console.error("Unknown Type")
        }
        setNodeElements(newNodeElements)
    }

    return (
        <div className="App">
            <TabContext value={tabVal}>
                {// @ts-ignore
                }<AppBar position="static">
                <h1 style={{userSelect: "none"}}>{getPipe()}Pipify</h1>
                <TabList onChange={(e, val) => setTabVal(val)} aria-label="simple tabs example">
                    <Tab label="Editor" value="1"/>
                    <Tab label="Meta Daten" value="2"/>
                    <Tab label="Optimierung" value="3"/>
                </TabList>
            </AppBar>
                <TabPanel value="1">
                    <div className="react-flow-container">
                        <FlowContainer pipes={pipes} setPipes={setPipes}
                                       nodeElements={nodeElements} setNodeElements={setNodeElements} />
                        <VersionNumber/>
                        <NodeMenuSpawnerContainer onNewNode={handleNewNode}/>
                        <DetermineMassFlowRateButton grid={{pipes: (pipes as Pipe[]), ...nodeElements}} />
                    </div>
                </TabPanel>
                <TabPanel value="2">
                    <MetaDataContainer/>
                </TabPanel>
                <TabPanel value={"3"}>
                    <OptimizationResults />
                </TabPanel>
            </TabContext>
            {renderUpload ?
                <FileUpload loadGrid={(hwg) => {
                    setRenderUpload(false)
                    insertGrid(hwg)
                }}/> : <></>
            }

            {/* @ts-ignore*/}
            <FileDownload grid={{...nodeElements, pipes}} setRenderUpload={(val: boolean) => setRenderUpload(val)}/>
            <UserTour endTest={clearGrid} startTest={insertGrid}/>
            <Notifications />
        </div>
    );
}

export default App;
