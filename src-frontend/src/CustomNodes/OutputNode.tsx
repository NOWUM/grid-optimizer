import {customInputHandleStyle, CustomNodeDate} from "./InputNode";
import {Handle, Position} from "react-flow-renderer";
import {showNodeOutputDialog} from "../Overlays/NodeContextOverlay";

const customNodeStyles = {
    background: 'red',
    color: '#FFF',
    padding: 10,
};

export const OutputNode = ({ data } : {data: CustomNodeDate}) => {
    const handleClick = () => {
        showNodeOutputDialog("Bearbeiten sie diese Node", () => {}, () => {}, "123")
    }

    return (
        <div style={customNodeStyles} onDoubleClick={handleClick}>
            <Handle type="target" position={Position.Top} style={{ ... customInputHandleStyle}} />
            <div>{data.label}</div>
        </div>
    );
};
