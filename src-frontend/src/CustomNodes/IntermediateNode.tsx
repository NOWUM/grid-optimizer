import {Handle, Position} from "react-flow-renderer";
import {customInputHandleStyle, CustomNodeDate, customOutputHandleStyle} from "./InputNode";


const customNodeStyles = {
    background: 'orange',
    color: '#FFF',
    padding: 10,
};




export const IntermediateNode = ({ data } : {data: CustomNodeDate}) => {
    return (
        <div style={customNodeStyles}>
            <Handle type="target" position={Position.Top} style={{ ...customInputHandleStyle }} />
            <div>{data.label}</div>
            <Handle
                type="source"
                position={Position.Right}
                id="a"
                style={{ ...customOutputHandleStyle }}
            />
            <Handle
                type="source"
                position={Position.Bottom}
                id="b"
                style={{ ...customOutputHandleStyle }}
            />

            <Handle
                type="source"
                position={Position.Left}
                id="c"
                style={{ ...customOutputHandleStyle }}
            />
        </div>
    );
};
