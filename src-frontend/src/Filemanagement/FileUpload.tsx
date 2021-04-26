import React, {useCallback} from "react";
import {useDropzone} from 'react-dropzone';
import "./file-upload.css";
import {HotWaterGrid} from "../models";



interface UploadProps {
    loadGrid: (hwg: HotWaterGrid) => void
}

export const FileUpload = (props: UploadProps) => {
    const mapToJSON = (reader: any) => {
        const binaryStr = reader.result;
        // @ts-ignore
        const dataString = Array.from(new Uint8Array(binaryStr))
            .map((c) => String.fromCharCode(c))   // convert char codes to strings
            .join('');     // join values together;
        return JSON.parse(dataString);
    };

    const handleStateUpdate = (reader: any) => {
        const jsonResult = mapToJSON(reader);
        console.log(jsonResult)
        props.loadGrid(jsonResult)
    };

    const onDrop = useCallback((acceptedFiles) => {
        acceptedFiles.forEach((file: File) => {
            const reader = new FileReader();

            reader.onabort = () => console.log('file reading was aborted');
            reader.onerror = () => console.log('file reading has failed');
            reader.onload = () => {
                handleStateUpdate(reader);
            };
            reader.readAsArrayBuffer(file);
        });

    }, []);
    const {getRootProps, getInputProps} = useDropzone({onDrop});

    return (
        <div {...getRootProps({
            onClick: (event) => event.stopPropagation(),
            onDragEnter: (event) => console.log("DRAG ENTER")
        })} className={"upload-container"}>
            <input {...getInputProps({
                onClick: (event) => event.stopPropagation()
            })} className={"input-container"}/>
            <div className={"upload-info"}>
                Ziehe Dokument für Upload in die Fläche
            </div>
        </div>
    );
};
//@ts-ignore
