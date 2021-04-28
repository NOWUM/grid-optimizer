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
// you need these styles for React Flow to work properly
import 'react-flow-renderer/dist/style.css';

// additionally you can load the default theme
import 'react-flow-renderer/dist/theme-default.css';
import {EdgePopover} from "./Overlays/EdgePopover";
import {showEditPipeDialog} from "./Overlays/EdgeContextOverlay";
import {BaseNode, HotWaterGrid, NodeElements, NodeType, Pipe} from "./models";
import {InputNode} from './CustomNodes/InputNode';
import {IntermediateNode} from "./CustomNodes/IntermediateNode";
import {OutputNode} from "./CustomNodes/OutputNode";
import {createGrid} from "./utils/utility";
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
            'Content-Type': 'application/json',
            "Access-Control-Allow-Origin": "*"
        },
        body: JSON.stringify(grid)
    }
    return fetch('http://127.0.0.1:8080/api/grid/verify', configuration)
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

        verifyBackend(createGrid(nodeElements, pipes as Pipe[])).then((verified: boolean) => {
            if(verified) {
                showEditPipeDialog("Füge ein neues Rohr hinzu", () => {
                    params= {...params, ...edgeConfiguration}

                    //@ts-ignore
                    setPipes((els) => addEdge(params, els))
                }, () => console.log("Nothing to do here"), params.id)
            }
        }
        )
    };

    // @ts-ignore
    const onElementsRemove = (elementsToRemove) => setElements((els) => removeElements(elementsToRemove, els));

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

    const getElements = (): Elements => {
        const inputNodes = addTypeToNodes(nodeElements.inputNodes, NodeType.INPUT_NODE)
        const intermediateNodes = addTypeToNodes(nodeElements.intermediateNodes, NodeType.INTERMEDIATE_NODE)
        const outputNodes = addTypeToNodes(nodeElements.outputNodes, NodeType.OUTPUT_NODE)
        const defaultPipes = pipes.map((el) => {return {...el, ...edgeConfiguration}})

        return [...inputNodes, ...intermediateNodes, ...outputNodes, ...defaultPipes]
    }

    // @ts-ignore
    return <ReactFlow elements={getElements()}
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
