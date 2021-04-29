import React, {Dispatch, SetStateAction, useState} from 'react';
import ReactFlow, {
    addEdge,
    ArrowHeadType,
    Background,
    BackgroundVariant,
    Edge,
    Elements,
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
    NodeElements,
    NodeType,
    OutputNode as OutputNodeModel,
    Pipe
} from "./models";
import {InputNode} from './CustomNodes/InputNode';
import {IntermediateNode} from "./CustomNodes/IntermediateNode";
import {OutputNode} from "./CustomNodes/OutputNode";
import {baseUrl, createGrid} from "./utils/utility";
import {notify} from "./Overlays/Notifications";

const style = getComputedStyle(document.body)
const corpColor = style.getPropertyValue('--corp-main-color')

const edgeConfiguration = {
    animated: true,
    type: 'step',
    arrowHeadType: ArrowHeadType.ArrowClosed,
    style: {stroke: `rgb(${corpColor})`, strokeWidth: "3px"}
}


const nodeTypes = {
    INPUT_NODE: InputNode,
    INTERMEDIATE_NODE: IntermediateNode,
    OUTPUT_NODE: OutputNode
};


interface PopupProps {
    target: any,
    edge: Edge
}

interface FlowContainerProperties {
    pipes: Elements<Pipe>,
    setPipes: Dispatch<SetStateAction<Elements<Pipe>>>,
    nodeElements: NodeElements,
    setNodeElements: Dispatch<SetStateAction<NodeElements>>
}

enum ResultCode {
    OK = 200,
    INTERNAL_SERVER_ERROR = 500
}




const verifyBackend = (grid: HotWaterGrid): Promise<boolean> => {
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

export const FlowContainer = ({pipes, setPipes, nodeElements, setNodeElements}: FlowContainerProperties) => {



    const [popupTarget, setPopupTarget] = useState<PopupProps | null>(null)

    // @ts-ignore
    const onConnect = (params) => {
        console.log(params);
        params.animated = true;

        showEditPipeDialog("Füge ein neues Rohr hinzu", (id, length1) => {
            const pipesToVerify = [...pipes]
            const newPipe = {
                source: params.source,
                target: params.target,
                id, length: length1
            }

            pipesToVerify.push(newPipe)
            verifyBackend(createGrid(nodeElements, pipesToVerify as Pipe[])).then((verified: boolean) => {
                    if(verified) {
                        params= {...params, ...edgeConfiguration}
                        //@ts-ignore
                        setPipes((els) => addEdge(params, els))
                    }
                }
            )

        }, () => console.log("Nothing to do here"), params.id)


    };

    // @ts-ignore
    const onElementsRemove = (elementsToRemove) => setPipes((els) => removeElements(elementsToRemove, els));

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

    const handleEditEdge = () => {
        console.log(popupTarget)
    }

    const handleRemoveEdge = () => {
        onElementsRemove([popupTarget?.edge])
    }

    const addTypeToNodes = (nodes: BaseNode[], type: NodeType) => {
        return nodes.map((el) => {
            return {...el, type}
        })
    }

    const getElementsForFlow = (): Elements => {
        const inputNodes = addTypeToNodes(nodeElements.inputNodes, NodeType.INPUT_NODE)
        const intermediateNodes = addTypeToNodes(nodeElements.intermediateNodes, NodeType.INTERMEDIATE_NODE)
        const outputNodes = addTypeToNodes(nodeElements.outputNodes, NodeType.OUTPUT_NODE)
        const defaultPipes = pipes.map((el) => {
            return {...el, ...edgeConfiguration}
        })

        inputNodes.map((n) => {
            const {flowTemperatureTemplate, returnTemperatureTemplate} = (n as InputNodeModel)
            n.data = {
                ...n.data, flowTemperatureTemplate, returnTemperatureTemplate, updateNode
            }
        })

        intermediateNodes.map((n) => {
            n.data = {...n.data, updateNode}
        })

        outputNodes.map((n) => {
            const {thermalEnergyDemand, pressureLoss, loadProfileName} = (n as OutputNodeModel)
            n.data = {
                ...n.data, thermalEnergyDemand, pressureLoss, updateNode, loadProfileName
            }
        })

        return [...inputNodes, ...intermediateNodes, ...outputNodes, ...defaultPipes]
    }

    const updateNode = (newNode: BaseNode) => {
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


        const newNodeElements = {...nodeElements}
        // @ts-ignore
        newNodeElements[nodeType] = nodeElements[nodeType].map((n) => {
            if (n.id === newNode.id) {
                return newNode
            } return n
        })
        setNodeElements(newNodeElements)
    }

    // @ts-ignore
    return <ReactFlow elements={getElementsForFlow()}
                      onConnect={(params) => onConnect(params)}
                      nodeTypes={nodeTypes}
                      onEdgeContextMenu={onElementClick}
                      deleteKeyCode={46}
                      onClick={() => closePopupTarget()}
    >
        <Background
            variant={BackgroundVariant.Dots}
            gap={24}
            size={1}
        />
        <EdgePopover
            target={popupTarget?.target}
            onSplitEdge={() => handleSplitEdge()}
            onEditEdge={() => handleEditEdge()}
            onRemoveEdge={() => handleRemoveEdge()}
            targetId={popupTarget?.edge.id!}/>
    </ReactFlow>;

}
