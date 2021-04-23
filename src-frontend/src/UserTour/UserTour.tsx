import React, {useState} from 'react'
import Tour from 'reactour'
import {HotWaterGrid} from "../models";


const timeout = (ms: number) => new Promise(res => setTimeout(res, ms))


let haveStartedTest = false
const mock = require("../mock/GridMock.json")

export const UserTour = ({startTest, endTest}: { startTest: (mock: HotWaterGrid) => void, endTest: () => void }) => {
    const [isTourOpen, setIsTourOpen] = useState(false);

    const steps = [
        {
            selector: '.App',
            content: 'Ziehe ein bestehendes Dokument auf die Webseite',
        },
        {
            selector: '.react-flow-container',
            content: 'Hier kannst du später dein Grid bearbeiten',
        }, {
            action: () => {
                if (!haveStartedTest) {
                    startTest(mock)
                    haveStartedTest = true
                }
            },
            selector: '.react-flow-container',
            content: 'Wir haben jetzt schon mal ein Beispiel Grid erstellt',
        }, {
            selector: '.react-flow-container',
            content: 'Die Knotenpunkte könnt ihr an den kleinen Nubsis verbinden. Rot ist Eingang, blau ist Ausgang',
        }, {
            action: () => {
                if (haveStartedTest) {
                    endTest()
                    haveStartedTest = false
                }},
            selector: '.file-download-container',
            content: 'Hier kannst du das Projekt dann herunterladen',
        },
        // ...
    ];


    return (
        <>
            { /* other stuff */}
            <Tour
                steps={steps}
                isOpen={isTourOpen}
                onRequestClose={() => setIsTourOpen(false)}
            />
        </>
    )
};
