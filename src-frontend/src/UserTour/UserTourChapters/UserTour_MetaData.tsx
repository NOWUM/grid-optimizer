import {ReactourStep} from "reactour";

export const MetaDataChapter = () => [
    step1(), step2()
]
const step1 = (): ReactourStep => {
    return {
        content: 'In dieser Ansicht können die Metadaten hinzugefügt werden.',
        selector: '.App',
    }
}

const step2 = (): ReactourStep => {
    return {
        content: 'Hier können Rohrtypen erstellt und gelöscht werden.',
        selector: '.metadata-pipe-type',
    }
}
