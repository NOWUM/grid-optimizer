import React, {useCallback} from "react";
import {useDropzone} from 'react-dropzone';
import "./file-upload.css";
import {HotWaterGrid, instanceOfHotWaterGrid} from "../models";
import {notify} from "../ReactFlow/Overlays/Notifications";
import {Cancel} from "@material-ui/icons";


interface UploadProps {
    loadGrid: (hwg: HotWaterGrid) => void
    cancel: () => void
}

export const FileUpload = (props: UploadProps) => {
    const mapToJSON = (reader: FileReader) => {
        const binaryStr = reader.result;

        const dataString = Array.from(new Uint8Array(binaryStr as ArrayBufferLike))
            .map((c) => String.fromCharCode(c))   // convert char codes to strings
            .join('');     // join values together;
        return JSON.parse(dataString);
    };

    const mapCSVToArray = (reader: FileReader) => {
        const binaryStr = reader.result;
        console.log(binaryStr)
        return Array.from(new Uint8Array(binaryStr as ArrayBufferLike))
            .map((c) => String.fromCharCode(c))   // convert char codes to strings
            .join('') // join values together;
            .split("\n") // seperates at line breaks
            .map((c)=> c.trim()) // trims leading and trailing whitespaces
            .filter( (c) => c.match("^-?[0-9]+\.[0-9]*$")); // Filter all non numbers

    }

    const handleStateUpdate = (reader: FileReader, fileType: string) => {
        console.log(fileType)
        if(fileType === "text/csv") {
            handleCSVUpload(reader)
        } else if (fileType === "application/json") {
            handleJSONUpload(reader)
        }
    };

    const handleJSONUpload = (reader: FileReader) => {
        const jsonResult = mapToJSON(reader);
        console.log(instanceOfHotWaterGrid(jsonResult))
        if(instanceOfHotWaterGrid(jsonResult)) {
            props.loadGrid(jsonResult)
        } else {
            notify("Die Eingabedatei ist leider nicht valide")
        }
    }

    //@ts-ignore
    const handleCancelClick = (e: any) => {
        e.stopPropagation();
        props.cancel()
    }

    const handleCSVUpload = (reader: FileReader) => {
        const csvStr = mapCSVToArray(reader)
    }

    const onDrop = useCallback((acceptedFiles) => {
        acceptedFiles.forEach((file: File) => {
            const fileType = file.type
            const reader = new FileReader();

            reader.onabort = () => console.log('file reading was aborted');
            reader.onerror = () => console.log('file reading has failed');
            reader.onload = () => {
                handleStateUpdate(reader, fileType);
            };
            reader.readAsArrayBuffer(file);
        });

    }, []);
    const {getRootProps, getInputProps} = useDropzone({onDrop});

    return (
        <div {...getRootProps({
            // onClick: (event) => event.stopPropagation(),
            onDragEnter: (event) => console.log("DRAG ENTER")
        })} className={"upload-container"}>
            <input {...getInputProps({
                onClick: (event) => event.stopPropagation()
            })} className={"input-container"}/>
            <div id={"white-bg"} />
            <Cancel onClick={handleCancelClick} style={{color: "red"}} id={"cancel-icon"}/>
            <div className={"upload-info"}>
                Ziehe Dokument für Upload in die Fläche
            </div>
        </div>
    );
};
//@ts-ignore
