export const OptimizationChapter = () => {
    return [step1(), step2(), step3(), step4()]
}

const step1 = () => {
    return {
        selector: '.App',
        content: 'In dieser Ansicht können die Ergebnisse der Optimierung im Detail begutachtet werden.',
    }
}

const step2 = () => {
    return {
        selector: '.download-report',
        content: 'Mit diesem Knopf kann der Bericht als .xls-Datei heruntergeladen werden. ',
    }
}

const step3 = () => {
    return {
        selector: '.costs',
        content: 'Hier können die resultierenden Kostenparameter begutachtet werden.',
    }
}

const step4 = () => {
    return {
        selector: '.optimization-graphs',
        content: 'Die Ergebnisse pro Knoten und Rohr können hier aufgeklappt und begutachtet werden.',
    }
}
