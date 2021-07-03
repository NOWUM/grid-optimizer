import {ReactourStep} from "reactour";
import {HotWaterGrid} from "../../models/models";
import {OptimizationStatusResponse} from "../../models/dto-models";

let haveStartedTest = false

let savedGrid: HotWaterGrid;
let savedOptimizationStatus: OptimizationStatusResponse | undefined

interface Properties {
    startTest: (mock: HotWaterGrid, o: OptimizationStatusResponse) => void,
    endTest: () => void,
}

const gridMock = require("../../mock/GridMock.json")
const optimizationStatusMock = require("../../mock/OptimizationStatusMock.json")

export const EditorChapter = (): ReactourStep[] => [
    step1(),
    step2(),
    step3(),
    step4(),
    step5(),
    step6(),
    step7(),
    step9()
]

export const EditorAfterOpen = (startTest: (mock: HotWaterGrid, o: OptimizationStatusResponse) => void, grid: HotWaterGrid, optimizationStatus?: OptimizationStatusResponse) => {
    if (!haveStartedTest) {
        savedGrid = grid;
        haveStartedTest = true;
        savedOptimizationStatus = optimizationStatus;
        startTest(gridMock, optimizationStatusMock)
    }
}

export const EditorBeforeClose = (endTest: (grid: HotWaterGrid, o?: OptimizationStatusResponse) => void) => {
    if (haveStartedTest) {
        endTest(savedGrid, savedOptimizationStatus)
        haveStartedTest = false
    }
}

const step1 = () => {
    return {
        selector: '.react-flow-container',
        content: 'Wir haben jetzt schon mal ein Beispiel Grid erstellt.',
    }
}


const step2 = () => {
    return {
        selector: '.react-flow-container',
        content: 'Die Netzelemente können mit den kleinen Nubsis verbunden werden. Rot ist Eingang, blau ist Ausgang.',
    }
}

const step3 = () => {
    return {
        selector: '.node-menu-container',
        content: 'Hier können neue Netzelemente angelegt werden.',
    }
}

const step4 = () => {
    return {
        selector: '.optimize-button',
        content: 'Wurde das Netz fertig erstellt, kann es hier optimiert.',
    }
}

const step5 = () => {
    return {
        selector: '.optimization-progress',
        content: 'Nach klick auf den Knopf kann hier der Fortschritt überwacht werden.',
    }
}

const step6 = () => {
    return {
        selector: '.react-flow-container',
        content: 'Wurde die Optimierung abgeschlossen, werden hier der längste und der kritische Pfad hervorgehoben.',
    }
}

const step7 = () => {
    return {
        selector: '.optimization-tab',
        content: 'Jetzt ist auch der Optimierungsreiter freigeschaltet. Hier können die Ergebnisse nochmal im Detail betrachtet werden.',
    }
}

const step9 = () => {
    return {
        selector: '.file-download-container',
        content: 'Hier kannst du das Netz herunterladen und zu einem späteren Zeitpunkt wieder hochladen.',
    }
}
