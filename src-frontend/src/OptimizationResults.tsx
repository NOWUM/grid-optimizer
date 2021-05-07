import React from "react";
// @ts-ignore
import Plot from 'react-plotly.js';
import {MassenstromResponse} from "./models";
// const temps =  require("./mock/temperature-mock")

export const OptimizationResults = ({massenstrom}: {massenstrom: MassenstromResponse}) => {
    const getPlotData = () => {
        return [
            {
                y: massenstrom.energyHeatDemand,
                type: 'scatter',
                mode: 'lines+markers',
                marker: {color: 'red'},
                name: "Energie Heat Demand"
            }, {
                y: massenstrom.flowInTemperatures,
                type: 'scatter',
                mode: 'lines+markers',
                marker: {color: 'green'},
                name: "Flow In Temperature"
            },{
                y: massenstrom.flowOutTemperatures,
                type: 'scatter',
                mode: 'lines+markers',
                marker: {color: 'blue'},
                name: "Flow Out Temperature"
            },{
                y: massenstrom.massenstrom,
                type: 'scatter',
                mode: 'lines+markers',
                marker: {color: 'orange'},
                name: "Massenstrom"
            },{
                y: massenstrom.temperatures,
                type: 'scatter',
                mode: 'lines+markers',
                marker: {color: 'brown'},
                name: "Temperaturen"
            },
        ]
    }
 return <Plot
     data={getPlotData()}
     layout={ {width: "90vw", height: "80vh", title: 'Temperaturverlauf'} }
 />
}
