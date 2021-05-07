import {confirmAlert} from "react-confirm-alert";
import React, {useEffect, useState} from "react";
import {BaseNode, InputNode, IntermediateNode, NodeType, OutputNode} from "../models";
import {Grid, TextField} from "@material-ui/core";
import {notify} from "./Notifications";
import {baseUrl} from "../utils/utility";
import {FormSkeleton} from "./FormSkeleton";
import {CustomSelect} from "../Components/CustomSelect";


export const showNodeInputDialog = (message: string,
                                    node: InputNode,
                                    onConfirm: (node: InputNode) => void,
                                    onAbort: () => void) => {

    showNodeDialog(message, (n) => onConfirm(n as InputNode), onAbort, node)
}

export const showNodeOutputDialog = (message: string,
                                     node: OutputNode,
                                     onConfirm: (node: OutputNode) => void,
                                     onAbort: () => void) => {

    showNodeDialog(message, (n) => onConfirm(n as OutputNode), onAbort, node)
}

export const showNodeIntermediateDialog = (message: string,
                                           node: IntermediateNode,
                                           onConfirm: (node: IntermediateNode) => void,
                                           onAbort: () => void) => {

    showNodeDialog(message, (n) => onConfirm(n as IntermediateNode), onAbort, node)
}

export const defaultGetConfiguration = {
    method: 'GET',
    contentType: "application/json",
    accept: "application/json"
}

export const showNodeDialog = (message: string,
                               onConfirm: (node: BaseNode) => void,
                               onAbort: () => void, node: BaseNode) => {

    let component: { (arg0: () => void): boolean | React.ReactChild | React.ReactFragment | React.ReactPortal | null | undefined; (onClose: () => void): JSX.Element; (onClose: () => void): JSX.Element; (onClose: () => void): JSX.Element; }
    switch (node.type) {
        case NodeType.INPUT_NODE:
            component = (onClose: () => void) => <InputNodeForm message={message}
                                                                onConfirm={(newNode: InputNode) => {
                                                                    onConfirm(newNode)
                                                                    onClose()
                                                                }}
                                                                onAbort={() => {
                                                                    onAbort()
                                                                    onClose()
                                                                }} node={node as InputNode}
            />
            break;
        case NodeType.INTERMEDIATE_NODE: component = (onClose: () => void) => <IntermediateNodeForm message={message}
                                                        onConfirm={(newNode: IntermediateNode) => {
                                                            onConfirm(newNode)
                                                            onClose()
                                                        }}
                                                        onAbort={() => {
                                                            onAbort()
                                                            onClose()
                                                        }} node={node as IntermediateNode}
        />
            break;
        case NodeType.OUTPUT_NODE:
            component = (onClose: () => void) => <OutputNodeForm message={message}
                                                                 onConfirm={(newNode: OutputNode) => {
                                                                     onConfirm(newNode)
                                                                     onClose()
                                                                 }}
                                                                 onAbort={() => {
                                                                     onAbort()
                                                                     onClose()
                                                                 }} node={node as OutputNode}
            />
            break;
        default:
            console.error("Unknown Node Type")
            component = (onClose: () => void) => <></>;
    }


    confirmAlert({
        customUI: ({onClose}) => component(onClose)
    })
}


// : Promise<string[]>
const fetchLoadProfileOptions = () => {


    return fetch(`${baseUrl}/api/profiles/names`, defaultGetConfiguration)
        .then(response => {
            return response.json()
        })
        .catch(e => {
            console.error(e)
        });
}

const OutputNodeForm = ({message, onConfirm, onAbort, node}: {
    message: string
    onConfirm: (node: OutputNode) => void,
    onAbort: () => void,
    node: OutputNode
}) => {

    const [label, setLabel] = useState(node.data.label)
    const [thermalEnergyDemand, setThermalEnergyDemand] = useState<string>(`${node.thermalEnergyDemand}`)
    const [pressureLoss, setPressureLoss] = useState<string>(`${node.pressureLoss}`)
    const [loadProfileName, setLoadProfileName] = useState(node.loadProfileName)
    const [loadProfileOptions, setLoadProfileOption] = useState<string[]>([])
    const [selectOpen, setSelectOpen] = useState(false)

    useEffect(() => {
        fetchLoadProfileOptions().then(options => {
            if(options) {
                setLoadProfileOption(options)
            } else {
                notify("Load Profiles could not be fetched.")
            }
        })
    }, [])


    const submitNewNode = () => {
        if (validateInput()) {
            console.log(node)
            const newNode = {
                thermalEnergyDemand: Number(thermalEnergyDemand),
                pressureLoss: Number(pressureLoss),
                data: {label: label},
                // @ts-ignore
                position: {x: node.position?.x ?? node.xPos, y: node.position?.y ?? node.yPos},
                type: node.type,
                id: node.id,
                loadProfileName
            }
            onConfirm(newNode)
        } else {
            notify("Eingaben nicht valide")
        }
    }

    const validateInput = () => {
        const inputsAreNumbers = isNumeric(thermalEnergyDemand) && isNumeric(pressureLoss)
        const pressureLossIsInRange = (Number(pressureLoss) >= 0 && Number(pressureLoss) <= 1)

        return inputsAreNumbers && pressureLossIsInRange;
    }

    const isNumeric = (val: string) => {
        //@ts-ignore
        return !isNaN(val)
    }

    const handleSelectChange = (val: string) => {
        setLoadProfileName(val)
    }

    return <FormSkeleton id={node.id} message={message} onConfirm={submitNewNode} onAbort={onAbort}>
        <>
            <Grid container
                  direction="row" item xs={7} spacing={3}>
                <Grid item xs={12}>
                    <TextField id="standard-basic" label="Label" placeholder="OutputLabel"
                               value={label}
                               onChange={(val) => {
                                   setLabel(val.target.value)
                               }}/>
                </Grid>
            </Grid>
            <Grid container
                  direction="row" item xs={7} spacing={3}>
                <Grid item xs={12}>
                    <TextField id="standard-basic" label="Warmwasserbedarf [Kwh]" type="number"
                               value={thermalEnergyDemand}
                               onChange={(val) => setThermalEnergyDemand(val.target.value)}
                               placeholder="127.30"/>
                </Grid>
            </Grid>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Druckverluste" type="number" placeholder="0.30"
                           helperText={"Der Druck muss zwischen 0 und 1 Bar liegen."}
                           value={pressureLoss}
                           onChange={(val) =>{
                               setPressureLoss(val.target.value)
                           }}/>
            </Grid>
        </Grid>


        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <CustomSelect value={loadProfileName} options={loadProfileOptions} onValueChange={handleSelectChange}/>
            </Grid>
        </Grid>
        </>
    </FormSkeleton>
}


const InputNodeForm = ({message, onConfirm, onAbort, node}: {
    message: string
    onConfirm: (node: InputNode) => void,
    onAbort: () => void,
    node: InputNode
}) => {

    const [label, setLabel] = useState(node.data.label)
    const [flowTemperatureTemplate, setFlowTemperatureTemplate] = useState<string>(node.flowTemperatureTemplate)
    const [returnTemperatureTemplate, setReturnTemperatureTemplate] = useState<string>(node.returnTemperatureTemplate)

    const submitNewNode = () => {
        if (validateInput()) {
            console.log(node)
            const newNode = {
                flowTemperatureTemplate,
                returnTemperatureTemplate,
                data: {label: label},
                // @ts-ignore
                position: {x: node.position?.x ?? node.xPos, y: node.position?.y ?? node.yPos},
                type: node.type,
                id: node.id
            }
            onConfirm(newNode)
        } else {
            notify("Eingaben nicht valide")
        }
    }

    // TODO Validation
    const validateInput = () => {
        return true;
    }

    return <FormSkeleton id={node.id} message={message} onAbort={onAbort} onConfirm={submitNewNode}>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Label" placeholder="OutputLabel"
                           value={label}
                           onChange={(val) => {
                               setLabel(val.target.value)
                           }}/>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Warmwasserbedarf [Kwh]"
                           value={flowTemperatureTemplate}
                           onChange={(val) => setFlowTemperatureTemplate(val.target.value)}
                           placeholder="3x+5"/>
            </Grid>
        </Grid>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Druckverluste" placeholder="7x^2+8"
                           value={returnTemperatureTemplate}
                           onChange={(val) => {
                               setReturnTemperatureTemplate(val.target.value)
                           }}/>
            </Grid>
        </Grid>


    </FormSkeleton>
}


const IntermediateNodeForm = ({message, onConfirm, onAbort, node}: {
    message: string
    onConfirm: (node: IntermediateNode) => void,
    onAbort: () => void,
    node: IntermediateNode
}) => {

    const [label, setLabel] = useState(node.data.label)

    const submitNewNode = () => {
        const newNode: IntermediateNode = {
            data: {label: label},
            // @ts-ignore
            position: {x: node.position?.x ?? node.xPos, y: node.position?.y ?? node.yPos},
            type: node.type,
            connect_limit: 3,
            id: node.id
        }
        onConfirm(newNode)
    }

    return <FormSkeleton id={node.id} message={message} onConfirm={submitNewNode} onAbort={onAbort}>

        <Grid container
              direction="row" item xs={7} spacing={3}>
            <Grid item xs={12}>
                <TextField id="standard-basic" label="Label" placeholder="OutputLabel"
                           value={label}
                           onChange={(val) => {
                               setLabel(val.target.value)
                           }}/>
            </Grid>
        </Grid>

    </FormSkeleton>
}
