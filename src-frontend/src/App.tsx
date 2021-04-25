import React, {useEffect, useState} from 'react';
import './App.css';
import {FlowContainer} from "./FlowContainer";
import {FileUpload} from "./Filemanagement/FileUpload";
import {uploadDropboxInit} from "./utils/utility";
import {BaseNode, HotWaterGrid, IntermediateNode, NodeElements, NodeType, OutputNode, Pipe} from "./models";
import {FileDownload} from "./Filemanagement/FileDownload";
import Notifications from "./Overlays/Notifications";
import {Elements} from "react-flow-renderer";
import {UserTour} from "./UserTour/UserTour";
import {VersionNumber} from "./VersionNumber";
import "@material-ui/core/"
import {NodeMenuSpawnerContainer} from "./NodeMenu/NodeMenuSpawnerContainer";


function App() {

    const [renderUpload, setRenderUpload] = useState<boolean>(false);
    // const [grid, setGrid] = useState<HotWaterGrid>({inputNodes: [], outputNodes: [], intermediateNodes: [], pipes: []})

    const [nodeElements, setNodeElements] = useState<NodeElements>({inputNodes: [], intermediateNodes: [], outputNodes: []});
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
        setNodeElements({ inputNodes: [], intermediateNodes: [], outputNodes: [],})
        setPipes([])
    }

    const handleNewNode = (newNode: BaseNode) => {
        const newNodeElements = {...nodeElements};
        switch (newNode.type) {
            case NodeType.INPUT_NODE: newNodeElements.inputNodes.push(newNode)
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
            {renderUpload ?
                <FileUpload loadGrid={(hwg) => {
                    console.log(hwg)
                    insertGrid(hwg)
                }}/>: <></>
            }
            <div className="react-flow-container">
                <FlowContainer pipes={pipes} setPipes={setPipes}
                               nodeElements={nodeElements} setNodeElements={setNodeElements}
                />

                <VersionNumber />

                <NodeMenuSpawnerContainer onNewNode={handleNewNode}/>
            </div>
            {/* @ts-ignore*/}
            <FileDownload grid={{...nodeElements, pipes}} />
            <Notifications />
            <UserTour endTest={clearGrid} startTest={insertGrid}/>
        </div>
    );
}

export default App;
