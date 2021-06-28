import React, {Dispatch, SetStateAction, useState} from 'react';
import ReactFlow, {
    ArrowHeadType,
    Background,
    BackgroundVariant,
    Edge,
    Elements,
    FlowElement,
    Node,
    removeElements
} from 'react-flow-renderer';
import 'react-flow-renderer/dist/style.css';

import 'react-flow-renderer/dist/theme-default.css';
import {EdgePopover} from "./Overlays/EdgePopover";
import {showEditPipeDialog} from "./Overlays/EdgeContextOverlay";
import {
    BaseNode,
    HotWaterGrid,
    InputNode as InputNodeModel,
    InputNode as InputNodeProp,
    IntermediateNode as IntermediateNodeProp,
    NodeElements,
    NodeType,
    OutputNode as OutputNodeModel,
    OutputNode as OutputNodeProp,
    Pipe
} from "../models";
import {InputNode} from '../CustomNodes/InputNode';
import {IntermediateNode} from "../CustomNodes/IntermediateNode";
import {OutputNode} from "../CustomNodes/OutputNode";
import {baseUrl, createGrid} from "../utils/utility";
import {notify} from "./Overlays/Notifications";
import {DefaultEdge} from "../Components/DefaultEdge";
import IdGenerator from "../utils/IdGenerator";

const style = getComputedStyle(document.body)
const corpColor = style.getPropertyValue('--corp-main-color')

const edgeConfiguration = {
    animated: true,
    type: 'DEFAULT_EDGE',
    arrowHeadType: ArrowHeadType.ArrowClosed,
    style: {stroke: `rgb(${corpColor})`, strokeWidth: "6px"}
}


const nodeTypes = {
    INPUT_NODE: InputNode,
    INTERMEDIATE_NODE: IntermediateNode,
    OUTPUT_NODE: OutputNode
};

const edgeTypes = {
    DEFAULT_EDGE: DefaultEdge
}


interface PopupProps {
    target: any,
    edge: Edge
}

interface FlowContainerProperties {
    pipes: Elements<Pipe>,
    setPipes: Dispatch<SetStateAction<Elements<Pipe>>>,
    nodeElements: NodeElements,
    setNodeElements: Dispatch<SetStateAction<NodeElements>>,
    temperatureSeries: string,
    setActiveOptimizationId: (id: string) => void
}

export enum ResultCode {
    OK = 200,
    INTERNAL_SERVER_ERROR = 500
}




export const verifyBackend = (grid: HotWaterGrid): Promise<boolean> => {
    const configuration = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(grid)
    }

    return fetch(`${baseUrl}/api/grid/verify`, configuration)
        .then(response => {
            response.text().then((text) => {
                if(text){
                    notify(text)
                }
            })
            return response.status}).then( (status) => {return status===ResultCode.OK} )
        .catch(e => {
            return false});
}

export const FlowContainer = ({
                                  pipes,
                                  setPipes,
                                  nodeElements,
                                  setNodeElements,
                                  temperatureSeries,
                                  setActiveOptimizationId
                              }: FlowContainerProperties) => {

    const [popupTarget, setPopupTarget] = useState<PopupProps | null>(null)

    // @ts-ignore
    const onConnect = (params) => {
        params.animated = true;
        showEditPipeDialog("Füge ein neues Rohr hinzu",
            (id, length1, coverageHeight) => {
                onConfirmPipe(params, id, length1, coverageHeight)
            },
            () => console.log("Nothing to do here"), params.id, undefined, undefined)
    };

    const onConfirmPipe = (params: any, id: string, length: number, coverageHeight: number) => {
        const pipesToVerify = [...pipes]
        const {source, target} = params;
        id = IdGenerator.getNextPipeId(source, target)

        const newPipe = {
            source: source,
            target: target,
            id, length, coverageHeight
        }

        pipesToVerify.push(newPipe)
        verifyBackend(createGrid(nodeElements, pipesToVerify as Pipe[], temperatureSeries)).then((verified: boolean) => {
                if (verified) {
                    params = {...params, ...edgeConfiguration, id, length, coverageHeight, data: {length, coverageHeight,}}
                    const newPipes = [...pipes]
                    newPipes.push(params)
                    setPipes(newPipes)
                }
            }
        )
    }

    const onElementsRemove = (elementsToRemove: any) => setPipes((els) => removeElements(elementsToRemove, els));

    const onElementClick = (event: any, edge: Edge) => {
        // showEdgeDialog("Gib bitte ein paar Rohrdaten an", () => console.log("confirm"), () => console.log())
        event.preventDefault()
        if(event.currentTarget) {
            // @ts-ignore
            setPopupTarget({target: event.currentTarget!, edge})
        }
    }

    const closePopupTarget = () => {
        setPopupTarget(null)
    }

    const handleSplitEdge = () => {
        console.log(popupTarget)
    }

    const handleEditEdge = (id: string, length: number, coverageHeight: number) => {

        const newPipes = pipes.map(p => {
            if(p.id === id) {
                return {...p, length, coverageHeight, data: {...p.data, length, coverageHeight}}
            }
        })

        setPipes(newPipes as Elements<Pipe>)
    }

    const handleRemoveEdge = () => {
        onElementsRemove([popupTarget?.edge])
    }

    const addTypeToNodes = (nodes: BaseNode[], type: NodeType) => {
        return nodes.map((el) => {
            return {...el, type}
        })
    }

    const handleDeleteNode = (id: string) => {
        const remainingPipes = (pipes as Pipe[]).filter(
            (p: FlowElement<Pipe>) => (p as Pipe).source !== id && (p as Pipe).target !== id)
        setPipes([...remainingPipes])
        setNodeElements({
            inputNodes: (getRemainingNodes(nodeElements.inputNodes, id) as InputNodeProp[]),
            outputNodes: (getRemainingNodes(nodeElements.outputNodes, id) as OutputNodeProp[]),
            intermediateNodes: (getRemainingNodes(nodeElements.intermediateNodes, id) as IntermediateNodeProp[])
        })

    }

    const getRemainingNodes = (nodes: BaseNode[], id: string) => {
        return [...nodes.filter(n => n.id !== id)]
    }

    const getElementsForFlow = (): Elements => {
        const inputNodes = addTypeToNodes(nodeElements.inputNodes, NodeType.INPUT_NODE)
        const intermediateNodes = addTypeToNodes(nodeElements.intermediateNodes, NodeType.INTERMEDIATE_NODE)
        const outputNodes = addTypeToNodes(nodeElements.outputNodes, NodeType.OUTPUT_NODE)
        // console.log(pipes)
        const defaultPipes = pipes.map((el) => {
            return {...el, ...edgeConfiguration, data: {...el.data, onCtrlClick: setActiveOptimizationId}}
        })

        const getCommonProps = (n: BaseNode) => {
            const {annualEnergyDemand, maximalNeededPumpPower, maximalPressureLoss} = n

            return {
                updateNode, onDelete: handleDeleteNode, annualEnergyDemand, maximalNeededPumpPower, maximalPressureLoss,
                onCtrlClick: setActiveOptimizationId
            }
        }

        inputNodes.forEach((n) => {
            const {flowTemperatureTemplate, returnTemperatureTemplate} = (n as InputNodeModel)
            n.data = {
                ...n.data, flowTemperatureTemplate, returnTemperatureTemplate, ...getCommonProps(n)
            }
        })

        intermediateNodes.forEach((n) => {
            n.data = {...n.data, ...getCommonProps(n)}
        })

        outputNodes.forEach((n) => {
            const {thermalEnergyDemand, pressureLoss, loadProfileName, replicas} = (n as OutputNodeModel)
            n.data = {
                ...n.data,
                thermalEnergyDemand,
                pressureLoss,
                loadProfileName,
                replicas,
                ...getCommonProps(n)
            }
        })

        return [...inputNodes, ...intermediateNodes, ...outputNodes, ...defaultPipes]
    }

    const updateNode = (newNode: BaseNode) => {

        //todo modify id and edge ids

        let nodeType;
        switch (newNode.type) {
            case NodeType.INPUT_NODE:
                nodeType = "inputNodes"
                break;
            case NodeType.INTERMEDIATE_NODE:
                nodeType = "intermediateNodes"
                break;
            case NodeType.OUTPUT_NODE:
                nodeType = "outputNodes"
                break;
            default:
                notify("Node to be updated cant be found")
                return;
        }

        const onLabelChange = (newNode: BaseNode, oldNode: BaseNode) => {
            const oldLabel = oldNode.data.label
            const newLabel = newNode.data.label
            if (oldLabel !== newLabel) {
                newNode.id = IdGenerator.getNextNodeId(newNode.data.label)

                pipes.map((p: FlowElement<Pipe>) => {
                    const {source, target} = (p as Pipe)
                    if (source === oldLabel || target === oldLabel) {
                        (p as Pipe).source = source === oldLabel ? newLabel : source;
                        (p as Pipe).target = target === oldLabel ? newLabel : target
                        const newSource = (p as Pipe).source
                        const newTarget = (p as Pipe).target
                        p.id = IdGenerator.getNextPipeId(source, target)
                    }
                    return p
                })

            }

            return newNode
        }

        const newNodeElements = {...nodeElements}
        // @ts-ignore
        newNodeElements[nodeType] = nodeElements[nodeType].map((oldNode) => {
            if (oldNode.id === newNode.id) {
                newNode = onLabelChange(newNode, oldNode);
                return newNode
            }
            return oldNode
        })

        verifyBackend({pipes: pipes as Pipe[], ...nodeElements, temperatureSeries}).then(b => {
            if (b) {
                setNodeElements(newNodeElements)
            }
        })
    }

    const handleNodeDragStop = (n: Node) => {
        const type: NodeType = n.type as NodeType;
        let property;
        const newNode = {...n.data, ...n};

        switch (type) {
            case NodeType.INPUT_NODE:
                property = "inputNodes";
                break;
            case NodeType.INTERMEDIATE_NODE:
                property = "intermediateNodes"
                break;
            case NodeType.OUTPUT_NODE:
                property = "outputNodes";
                break;
            default:
                console.log("ERROR UNKNOWN NODE TYPE")
        }
        // @ts-ignore
        const index = nodeElements[property].findIndex((nEle) => newNode.id === nEle.id)
        // @ts-ignore
        nodeElements[property][index] = newNode;
        setNodeElements(nodeElements)
    }

    return <ReactFlow elements={getElementsForFlow()}
                      onConnect={(params) => onConnect(params)}
                      onNodeDragStart={(e) => e.stopPropagation()}
                      onNodeDrag={(e) => e.stopPropagation()}
                      onNodeDragStop={(e, n: Node) => handleNodeDragStop(n)}
                      nodeTypes={nodeTypes}
                      edgeTypes={edgeTypes}
                      onEdgeContextMenu={onElementClick}
                      deleteKeyCode={46}
                      onClick={(e) => closePopupTarget()}
    >
        <Background
            variant={BackgroundVariant.Dots}
            gap={24}
            size={1}
        />
        <EdgePopover
            target={popupTarget?.target}
            onSplitEdge={() => handleSplitEdge()}
            onEditEdge={handleEditEdge}
            onRemoveEdge={() => handleRemoveEdge()}
            pipe={pipes.find(p => p.id === popupTarget?.edge.id!)! as Pipe} />
    </ReactFlow>;

}
