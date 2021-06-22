import React, {memo} from "react";
// @ts-ignore
import Plot from 'react-plotly.js';
import {MassenstromResponse} from "./models";

export const OptimizationResultsComponent = ({massenstrom}: {massenstrom: MassenstromResponse}) => {
    const getPlotData = () => {
        return [
            {
                y: massenstrom.energyHeatDemand,
                type: "scattergl",
                mode: 'lines+markers',
                marker: {color: 'red'},
                name: "Energie Heat Demand [kWh]"
            },
            {
                y: massenstrom.massenstrom,
                type: "scattergl",
                mode: 'lines+markers',
                marker: {color: 'orange'},
                name: "Massenstrom in [kg]"
            },{
                y: massenstrom.temperatures,
                type: "scattergl",
                mode: 'lines+markers',
                marker: {color: 'brown'},
                name: "Temperaturen [°C]"
            },
        ]
    }
 return <Plot
     data={getPlotData()}
     style={{ width: '100%', height: '100%' }}
     layout={ {autosize: true, title: 'Maximaler Massenstrom', xaxis: { title: 'Stunde im Jahr' } } }
 />
}

export const OptimizationResults = memo(OptimizationResultsComponent)
