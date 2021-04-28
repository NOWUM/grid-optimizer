import React from "react";
// @ts-ignore
import Plot from 'react-plotly.js';
const temps =  require("./mock/temperature-mock")

export const OptimizationResults = () => {
 return <Plot
     data={[
         {
             y: temps,
             type: 'scatter',
             mode: 'lines+markers',
             marker: {color: 'red'},
         },
         {type: 'bar', x: [1, 2, 3], y: [2, 5, 3]},
     ]}
     layout={ {width: "90vw", height: "80vh", title: 'Temperaturverlauf'} }
 />
}
