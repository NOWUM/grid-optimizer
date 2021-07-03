import React, {useState} from 'react'
import Tour, {ReactourStep} from 'reactour'
import {HotWaterGrid, TabEnum} from "../models/models";
import {MetaDataChapter} from "./UserTourChapters/UserTour_MetaData";
import {EditorAfterOpen, EditorBeforeClose, EditorChapter} from "./UserTourChapters/UserTour_Editor";
import {OptimizationChapter} from "./UserTourChapters/UserTour_Optimization";
import {notify} from "../ReactFlow/Overlays/Notifications";
import {OptimizationStatusResponse} from "../models/dto-models";


const timeout = (ms: number) => new Promise(res => setTimeout(res, ms))

const mock = require("../mock/GridMock.json")

interface Properties {
    userTourSetting: (mock: HotWaterGrid, optimizationStatus?: OptimizationStatusResponse) => void,
    setUserTourActive: (a: boolean) => void
    activeTab: TabEnum,
    grid: HotWaterGrid,
    userTourActive: boolean,
    optimizationStatus?: OptimizationStatusResponse
}

export const UserTour = ({userTourSetting, userTourActive, setUserTourActive, activeTab, grid, optimizationStatus}: Properties) => {

    const [currStep, setCurrStep] = useState(0)

    const getSteps = (): ReactourStep[] => {
        switch (activeTab) {
            case TabEnum.FORMULA_CHECK:
                return [];
            case TabEnum.META_DATA:
                return MetaDataChapter();
            case TabEnum.EDITOR:
                return EditorChapter();
            case TabEnum.OPTIMIZATION:
                return OptimizationChapter();
        }
    }

    const handleAfterOpen = (): void => {
        switch (activeTab) {
            case TabEnum.FORMULA_CHECK:
                break;
            case TabEnum.META_DATA:
                break;
            case TabEnum.EDITOR:
                EditorAfterOpen(userTourSetting, grid, optimizationStatus);
                break;
            case TabEnum.OPTIMIZATION:
        }
    }

    const handleBeforeClose = (): void => {
        switch (activeTab) {
            case TabEnum.FORMULA_CHECK:
                break;
            case TabEnum.META_DATA:
                break;
            case TabEnum.EDITOR:
                EditorBeforeClose(userTourSetting);
                break;
            case TabEnum.OPTIMIZATION:
                break;
        }
    }

    return (
        <>
            <Tour
                startAt={0}
                onAfterOpen={handleAfterOpen}
                onBeforeClose={handleBeforeClose}
                steps={getSteps()}
                isOpen={userTourActive}
                onRequestClose={() => setUserTourActive(false)}
            />
        </>
    )
};
