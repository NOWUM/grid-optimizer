import React, {useEffect, useState} from 'react';
import ReactFlow, {
    addEdge,
    ArrowHeadType,
    Background,
    BackgroundVariant,
    Edge, Elements,
    removeElements
} from 'react-flow-renderer';
// you need these styles for React Flow to work properly
import 'react-flow-renderer/dist/style.css';

// additionally you can load the default theme
import 'react-flow-renderer/dist/theme-default.css';
import {EdgePopover} from "./Overlays/EdgePopover";
import {showEditPipeDialog} from "./Overlays/EdgeContextOverlay";
import {BaseNode, Pipe} from "./models";


const style = getComputedStyle(document.body)
const corpColor = style.getPropertyValue('--corp-main-color')

const edgeConfiguration = {animated: true, type: 'step', arrowHeadType: ArrowHeadType.ArrowClosed, style: { stroke: `rgb(${corpColor})`, strokeWidth: "3px" }}

const initialElements = [
    {id: '1', data: {label: 'Sarah ist doof'}, position: {x: 250, y: 5}},
    // you can also pass a React component as a label
    {id: '2', data: {label: <div>Melanie auch</div>}, position: {x: 100, y: 100}},

    {id: '3', data: {label: <div>Node 3</div>}, position: {x: 500, y: 100}},
    {
        id: 'e1-2', source: '1', target: '2', label: 'Länge: 3 Meter', ...edgeConfiguration
    }
];
interface PopupProps {
    target: any,
    edge: Edge
}

export const FlowContainer = ({data}: {data: (BaseNode | Pipe)[]}) => {
    const [elements, setElements] = useState<Elements<any>>(initialElements);
    const [popupTarget, setPopupTarget] = useState<PopupProps | null>(null)

    useEffect(() => {
        console.log(data)
        setElements(data)
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

    // @ts-ignore
    return <ReactFlow elements={elements}
        onConnect={(params) => onConnect(params)}

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
