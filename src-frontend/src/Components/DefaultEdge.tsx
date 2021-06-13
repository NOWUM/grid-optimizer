import {getBezierPath, getMarkerEnd} from "react-flow-renderer";
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
    const edgePath = getBezierPath({ sourceX, sourceY, sourcePosition, targetX, targetY, targetPosition });
    return <>
            <>
                <path id={id} style={style} className="react-flow__edge-path" markerEnd={markerEnd} d={edgePath}/>
                <text>
                    <textPath href={`#${id}`} style={{fontSize: '12px'}} startOffset="50%" textAnchor="middle">
                         {data?.length? `Länge: ${data.length} \n`: ""}
                        {console.log(data?.diameter)}
                        {data?.diameter?`Durchschnitt: ${data?.diameter}`: ""}
                    </textPath>
                </text>
            </>
    </>
}
