import React, {Suspense, useEffect, useState} from 'react';
import './App.css';
import {FlowContainer, verifyBackend} from "./ReactFlow/FlowContainer";
import {FileUpload} from "./Filemanagement/FileUpload";
import {uploadDropboxInit} from "./utils/utility";
import {
    BaseNode,
    Costs,
    HotWaterGrid,
    InputNode,
    IntermediateNode,
    MassenstromResponse,
    NodeElements,
    NodeType,
    OptimizationMetadata,
    OutputNode,
    Pipe
} from "./models";
import {FileDownload} from "./Filemanagement/FileDownload";
import {Elements} from "react-flow-renderer";
import {UserTour} from "./UserTour/UserTour";
import {AppBar, Tab} from "@material-ui/core";
import {TabContext, TabList, TabPanel} from "@material-ui/lab";
import {MetaDataContainer} from "./MetaData/MetaDataContainer";
import {getPipe} from "./pipe";
import {VersionNumber} from "./VersionNumber";
import {NodeMenuSpawnerContainer} from "./ReactFlow/OverlayButtons/NodeMenu/NodeMenuSpawnerContainer";
import Notifications from "./ReactFlow/Overlays/Notifications";
import {OptimizationResults} from "./OptimizationResults";
import {DetermineMassFlowRateButton} from "./ReactFlow/OverlayButtons/NodeMenu/DetermineMassFlowRateButton";
import Backdrop from "./Backdrop";
import {KeyboardKey} from "./Components/ConfirmationButton";
import {Map, Storage, Timeline} from "@material-ui/icons";
import {
    defaultMassenstrom,
    defaultNodeElements,
    defaultOptimizationMetadata,
    defaultTemperatureKey
} from "./utils/defaults";
import {FormulaCheck} from "./FormulaCheck";
import {OptimizeButton} from "./ReactFlow/OverlayButtons/OptimizeButton";
import {CostView} from "./ReactFlow/OverlayButtons/CostView";
import {OptimizationNodeDetails} from "./OptimizationNode/OptimizationNodeDetails";

function App() {

    const [renderUpload, setRenderUpload] = useState<boolean>(false);
    const [tabVal, setTabVal] = useState("2")
    const [massenstrom, setMassenstrom] = useState<MassenstromResponse>(defaultMassenstrom)
    const [nodeElements, setNodeElements] = useState<NodeElements>(defaultNodeElements);
    const [pipes, setPipes] = useState<Elements<Pipe>>([])
    const [temperatureKey, setTemperatureKey] = useState<string>(defaultTemperatureKey)
    const [optimizationMetadata, setOptimizationMetadata] = useState<OptimizationMetadata>(defaultOptimizationMetadata)
    const [activeOptimizationId, setActiveOptimizationId] = useState<string | undefined>()


    const [costs, setCosts] = useState<Costs | undefined>(undefined)

    const handleKeyDown = (e: KeyboardEvent) => {

        if (e.key === KeyboardKey.ENTER || e.key === KeyboardKey.ESC) {
            e.preventDefault()
        }
    }

    useEffect(() => {
        if(activeOptimizationId !== undefined) {
            setTabVal("5")
        }
        console.log("activeOptimizationId: " + activeOptimizationId)
    },[activeOptimizationId])

    useEffect(() => {
        document.addEventListener('keydown', handleKeyDown, false);
        return () => {
            document.removeEventListener('keydown', handleKeyDown, false);
        }
    }, [])

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

    const deepCopyNodeElements = (): NodeElements => {
        return {inputNodes:[...nodeElements.inputNodes],
            intermediateNodes: [...nodeElements.intermediateNodes],
            outputNodes: [...nodeElements.outputNodes]}
    }

    const handleNewNode = (newNode: BaseNode) => {
        const newNodeElements = deepCopyNodeElements();
        switch (newNode.type) {
            case NodeType.INPUT_NODE: newNodeElements.inputNodes.push(newNode as InputNode)
                break;
            case NodeType.INTERMEDIATE_NODE: newNodeElements.intermediateNodes.push(newNode as IntermediateNode)
                break;
            case NodeType.OUTPUT_NODE: newNodeElements.outputNodes.push(newNode as OutputNode)
                break;
            default: console.error("Unknown Type")
        }
        verifyBackend({pipes: pipes as Pipe[], temperatureSeries: temperatureKey, ...newNodeElements})
            .then((isValid) => {
                if(isValid) {
                    setNodeElements(newNodeElements)
                }
            })
    }


    const getGrid = () => {
        return {pipes: (pipes as Pipe[]), ...nodeElements, temperatureSeries: temperatureKey}
    }

    const isMetaDataComplete = () => temperatureKey !== ""

    const isMaxMassenstromComplete = () => massenstrom.temperatures.length !== 0

    const isCostsComplete = () => !!costs

    const handleSetActiveOptimizationId = (id: string) => {
        console.log("Set active optimization id")
        console.log(id)
        setActiveOptimizationId(id)
        // setTabVal("5")
    }

    const handleSetTabVal = (val: string) => {
        setActiveOptimizationId(undefined)
        setTabVal(val)
    }

    return (
        <div className="App">
            <TabContext value={tabVal}>
                <AppBar position="static">
                    <h1 style={{userSelect: "none"}}>{getPipe()}Pipify<VersionNumber/></h1>
                    <TabList onChange={(e, val) => handleSetTabVal(val)} aria-label="simple tabs example">
                        <Tab icon={<Timeline/>} label="Formel Check" value="4"/>
                        <Tab icon={<Storage/>} label="Meta Daten" value="2"/>
                        <Tab icon={<Map/>} label="Editor" value="1" disabled={!isMetaDataComplete()}/>
                        <Tab icon={<Timeline/>} label="Max Massenstrom" value="3"
                             disabled={!isMaxMassenstromComplete()}/>
                        <Tab icon={<Timeline/>} label="Node Detail" value="5" disabled={!isCostsComplete()}/>
                    </TabList>
                </AppBar>
                <TabPanel value="1">
                    <div className="react-flow-container">
                        <FlowContainer pipes={pipes} setPipes={setPipes} nodeElements={nodeElements}
                                       setNodeElements={setNodeElements} temperatureSeries={temperatureKey}
                                       setActiveOptimizationId={handleSetActiveOptimizationId}/>
                        <NodeMenuSpawnerContainer onNewNode={handleNewNode}/>
                        <DetermineMassFlowRateButton grid={getGrid()} onResult={setMassenstrom}/>
                        <OptimizeButton grid={getGrid()} optimizationMetadata={optimizationMetadata} setCosts={setCosts}
                                        setPipes={setPipes} setNodeElements={setNodeElements}/>
                        <CostView costs={costs}/>
                    </div>
                </TabPanel>
                <TabPanel value="2">
                    <MetaDataContainer temperatureKey={temperatureKey} setTemperatureKey={setTemperatureKey}
                                       optimizationMetadata={optimizationMetadata}
                                       setOptimizationMetadata={setOptimizationMetadata}/>
                </TabPanel>
                <TabPanel value={"3"}>
                    <Suspense fallback={<Backdrop open={true}/>}>
                        <OptimizationResults massenstrom={massenstrom}/>
                    </Suspense>
                </TabPanel>
                <TabPanel value={"4"}>
                    <FormulaCheck />
                </TabPanel>
                <TabPanel value={"5"} id={"optimization-panel"}>
                    <OptimizationNodeDetails nodeElements={nodeElements} pipes={pipes as Pipe[]}
                                             activeId={activeOptimizationId}/>
                </TabPanel>

            </TabContext>
            {renderUpload ?
                <FileUpload
                    cancel={() => setRenderUpload(false)}
                    loadGrid={(hwg) => {
                        setRenderUpload(false)
                        insertGrid(hwg)
                    }}
                /> : <></>
            }

            {/* @ts-ignore*/}
            <FileDownload grid={{...nodeElements, pipes}} setRenderUpload={(val: boolean) => setRenderUpload(val)}/>
            <UserTour endTest={clearGrid} startTest={insertGrid}/>
            <Notifications />
        </div>
    );
}

export default App;
