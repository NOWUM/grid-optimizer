let nextId: number = 0;

const getNextPipeId = (sourceId: string, targetId: string) => {
    const result = `${sourceId}##${nextId}##${targetId}`
    increaseId()
    return result;
}

const increaseId = () => nextId += 1;

const getNextNodeId = (label: string) => {
    const result = `${nextId}+${label}`
    increaseId()
    return result
}

const IdGenerator = {
    setNextId: (id: number) => nextId = id,
    getNextPipeId,
    getNextNodeId
}

Object.freeze(IdGenerator)
export default IdGenerator;
