import {EdgeText, getBezierPath, getMarkerEnd, Position} from "react-flow-renderer";
import {Tooltip} from "@material-ui/core";
import React from "react";
import {notify} from "../ReactFlow/Overlays/Notifications";
import {handleNodeCtrlClick} from "../CustomNodes/InputNode";
import {Pipe} from "../models";


export interface GetCenterParams {
    sourceX: number;
    sourceY: number;
    targetX: number;
    targetY: number;
    sourcePosition?: Position;
    targetPosition?: Position;
}

const LeftOrRight = [Position.Left, Position.Right];

export const getCenter = ({
                              sourceX,
                              sourceY,
                              targetX,
                              targetY,
                              sourcePosition = Position.Bottom,
                              targetPosition = Position.Top,
                          }: GetCenterParams): [number, number, number, number] => {
    const sourceIsLeftOrRight = LeftOrRight.includes(sourcePosition);
    const targetIsLeftOrRight = LeftOrRight.includes(targetPosition);

    // we expect flows to be horizontal or vertical (all handles left or right respectively top or bottom)
    // a mixed edge is when one the source is on the left and the target is on the top for example.
    const mixedEdge = (sourceIsLeftOrRight && !targetIsLeftOrRight) || (targetIsLeftOrRight && !sourceIsLeftOrRight);

    if (mixedEdge) {
        const xOffset = sourceIsLeftOrRight ? Math.abs(targetX - sourceX) : 0;
        const centerX = sourceX > targetX ? sourceX - xOffset : sourceX + xOffset;

        const yOffset = sourceIsLeftOrRight ? 0 : Math.abs(targetY - sourceY);
        const centerY = sourceY < targetY ? sourceY + yOffset : sourceY - yOffset;

        return [centerX, centerY, xOffset, yOffset];
    }

    const xOffset = Math.abs(targetX - sourceX) / 2;
    const centerX = targetX < sourceX ? targetX + xOffset : targetX - xOffset;

    const yOffset = Math.abs(targetY - sourceY) / 2;
    const centerY = targetY < sourceY ? targetY + yOffset : targetY - yOffset;

    return [centerX, centerY, xOffset, yOffset];
};

export const DefaultEdge = ({
                                id,
                                sourceX,
                                sourceY,
                                targetX,
                                targetY,
                                sourcePosition,
                                targetPosition,
                                style,
                                data,
                                arrowHeadType,
                                markerEndId,
                            }: any) => {

    const markerEnd = getMarkerEnd(arrowHeadType, markerEndId);
    const edgePath = getBezierPath({sourceX, sourceY, sourcePosition, targetX, targetY, targetPosition});

    const [centerX, centerY] = getCenter({sourceX, sourceY, targetX, targetY, sourcePosition, targetPosition});

    const getLabel = () => {
        return <>
            <tspan>{data?.length ? `Länge: ${data.length} m; ` : ""}</tspan>
            <tspan>{data?.diameter ? `Durchmesser: ${data?.diameter} m` : ""}</tspan>
        </>
    }

    if (data?.isCritical) {
        style = {...style, stroke: "red"}
    }

    const text = (data?.diameter || data?.length) ? (
        <EdgeText
            x={centerX}
            y={centerY}
            label={getLabel()}
        />
    ) : null;
    return <>
        <Tooltip title={"Das ist ein Test"}>
            <>
                <path id={id} style={style} className="react-flow__edge-path" markerEnd={markerEnd} d={edgePath} onClick={() => handleNodeCtrlClick({data,id} as Pipe)}/>
                {/*<text>*/}
                {/*    <textPath href={`#${id}`} style={{fontSize: '12px'}} startOffset="50%" textAnchor="middle">*/}

                {/*    </textPath>*/}
                {/*</text>*/}
                {text}
            </>
        </Tooltip>
    </>
}
