// Opera 8.0+
// @ts-ignore
export const isOpera = (!!window.opr && !!opr.addons) || !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0;

// Firefox 1.0+
// @ts-ignore
export const isFirefox = typeof InstallTrigger !== 'undefined';

// Safari 3.0+ "[object HTMLElementConstructor]"
// @ts-ignore
export const isSafari = /constructor/i.test(window.HTMLElement) || ((p) => {
    return p.toString() === "[object SafariRemoteNotification]";
})// @ts-ignore
    (!window['safari'] || (typeof safari !== 'undefined' && safari.pushNotification));

// Internet Explorer 6-11
// @ts-ignore
export const isIE = false || !!document.documentMode;

// Edge 20+
export const isEdge = !isIE && !!window.StyleMedia;

// Chrome 1 - 79
// @ts-ignore
export const isChrome = !!window.chrome && (!!window.chrome.webstore || !!window.chrome.runtime);

// Edge (based on chromium) detection
export const isEdgeChromium = isChrome && (navigator.userAgent.indexOf("Edg") !== -1);

// Blink engine detection
export const isBlink = (isChrome || isOpera) && !!window.CSS;

export enum Browser {
    Chromium = "CHROMIUM", Firefox = "Firefox", Edge = "EDGE", Safari = "SAFARI"
}

export const getBrowser = (): Browser | undefined => {
    if (isEdgeChromium) {
        return Browser.Chromium;
    } else if (isFirefox) {
        return Browser.Firefox;
    } else if (isEdge) {
        return Browser.Edge;
    } else if (isSafari) {
        return Browser.Safari;
    }
};
