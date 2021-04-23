import {Handle, Position} from "react-flow-renderer";


const customNodeStyles = {
    background: 'green',
    color: '#FFF',
    padding: 10,
};

export const customInputHandleStyle = {
    background: "red", borderRadius: 0
}

export const customOutputHandleStyle = {
    background: "blue", borderRadius: 0
}

export interface CustomNodeDate {
    label: string | Element
}



export const InputNode = ({ data }: {data: CustomNodeDate}) => {
    return (
        <div style={customNodeStyles}>
            <Handle
                type="source"
                position={Position.Bottom}
                id="a"
                style={{ ...customOutputHandleStyle }}
            />
            <div>{data.label}</div>
        </div>
    );
};
