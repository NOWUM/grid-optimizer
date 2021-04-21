import {customInputHandleStyle, CustomNodeDate} from "./InputNode";
import {Handle, Position} from "react-flow-renderer";

const customNodeStyles = {
    background: 'red',
    color: '#FFF',
    padding: 10,
};

export const OutputNode = ({ data } : {data: CustomNodeDate}) => {
    return (
        <div style={customNodeStyles}>
            <Handle type="target" position={Position.Top} style={{ ... customInputHandleStyle}} />
            <div>{data.label}</div>
        </div>
    );
};
