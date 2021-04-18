import React, {useState} from 'react';
import ReactFlow, {
    addEdge,
    ArrowHeadType,
    Background,
    BackgroundVariant,
    Edge,
    removeElements
} from 'react-flow-renderer';
// you need these styles for React Flow to work properly
import 'react-flow-renderer/dist/style.css';

// additionally you can load the default theme
import 'react-flow-renderer/dist/theme-default.css';
import {showSplitEdgeDialog} from "./Overlays/EdgeContextOverlay";
import {EdgePopover} from "./Overlays/EdgePopover";

const initialElements = [
    {id: '1', data: {label: 'Sarah ist doof'}, position: {x: 250, y: 5}},
    // you can also pass a React component as a label
    {id: '2', data: {label: <div>Melanie auch</div>}, position: {x: 100, y: 100}},

    {id: '3', data: {label: <div>Node 3</div>}, position: {x: 500, y: 100}},
    {
        id: 'e1-2', source: '1', target: '2', animated: true, label: 'Länge: 3 Meter',
        type: 'step',
        arrowHeadType: ArrowHeadType.ArrowClosed, style: { stroke: '#CD2626', strokeWidth: "3px", arrowHeadStroke: '#FFD700' }
    }
];


export const FlowContainer = () => {
    const [elements, setElements] = useState(initialElements);
    const [popupTarget, setPopupTarget] = useState(null)

    // @ts-ignore
    const onConnect = (params) => {
        console.log(params);
        params.animated = true;
        showSplitEdgeDialog("", () => {
        }, () => console.log("Nothing to do here"))
        // @ts-ignore
        setElements((els) => addEdge(params, els))
    };

    // @ts-ignore
    const onElementsRemove = (elementsToRemove) => setElements((els) => removeElements(elementsToRemove, els));

    const onElementClick = (event: any, edge: Edge) => {
        // showEdgeDialog("Gib bitte ein paar Rohrdaten an", () => console.log("confirm"), () => console.log())
        event.preventDefault()
        console.log(event.currentTarget)
        setPopupTarget(event.currentTarget)
        console.log(edge)
    }

    const closePopupTarget = () => {
        setPopupTarget(null)
    }

    const handleSplitEdge = () => {

    }

    return <ReactFlow
        onConnect={(params) => onConnect(params)}
        elements={elements}
        onElementsRemove={onElementsRemove}
        onEdgeContextMenu={onElementClick}
        deleteKeyCode={46}
        onClick={closePopupTarget}>
        <Background
            variant={BackgroundVariant.Dots}
            gap={24}
            size={1}
        />
        <EdgePopover target={popupTarget} onSplitEdge={() => handleSplitEdge()}/>
    </ReactFlow>;

}
