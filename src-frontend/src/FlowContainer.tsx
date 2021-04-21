import React, {useEffect, useState} from 'react';
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


export const FlowContainer = ({data}: { data: HotWaterGrid }) => {

    const getNodeElements = (hwg: HotWaterGrid): NodeElements => {
        return {inputNodes: data.inputNodes, intermediateNodes: data.intermediateNodes, outputNodes: data.outputNodes}
    }

    const [nodeElements, setNodeElements] = useState<NodeElements>(getNodeElements(data));
    const [pipes, setPipes] = useState<Elements<Pipe>>(data.pipes)
    const [popupTarget, setPopupTarget] = useState<PopupProps | null>(null)

    useEffect(() => {
        console.log(data)
        setNodeElements(getNodeElements(data))
        setPipes(data.pipes)
    })


    // @ts-ignore
    const onConnect = (params) => {
        console.log(params);
        params.animated = true;
        showEditPipeDialog("Füge ein neues Rohr hinzu", () => {
            params= {...params, ...edgeConfiguration}

            //@ts-ignore
            setElements((els) => addEdge(params, els))
        }, () => console.log("Nothing to do here"), params.id)


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

        return [...inputNodes, ...intermediateNodes, ...outputNodes, ...pipes]
    }

    const getEmptyArrayIfUndefined = (el: (any[] | undefined)) => {
        return el ? el: []
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
