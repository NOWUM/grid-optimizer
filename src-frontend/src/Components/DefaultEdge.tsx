import {getMarkerEnd} from "react-flow-renderer";
import {Tooltip} from "@material-ui/core";

export const DefaultEdge = ({
                                id,
                                sourceX,
                                sourceY,
                                targetX,
                                targetY,
                                sourcePosition,
                                targetPosition,
                                style = {},
                                data,
                                arrowHeadType,
                                markerEndId,
                            }: any) => {

    const markerEnd = getMarkerEnd(arrowHeadType, markerEndId);

    return <>
        <Tooltip title={"Das ist ein Test"}>
            <>
                <path id={id} style={style} className="react-flow__edge-path" markerEnd={markerEnd}/>
                <text>
                    <textPath href={`#${id}`} style={{fontSize: '12px'}} startOffset="50%" textAnchor="middle">
                        {data.text}
                    </textPath>
                </text>
            </>
        </Tooltip>
    </>
}
