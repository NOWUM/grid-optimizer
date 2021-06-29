import React, { Component } from 'react';
import styled from 'styled-components';
import ee from "event-emitter"
import {red} from "@material-ui/core/colors";


const style = getComputedStyle(document.body)
const corpColor = style.getPropertyValue('--corp-secondary-color')

const Container = styled.div`
    color: black;
    background: rgb(var(--corp-secondary-color));
    padding: 16px;
    position: fixed;
    top: ${(props: { top: any; }) => props.top}px;
    right: 16px;
    z-index: 9999;
    transition: top 0.5s ease;
    border-radius: .25rem
    `;

// @ts-ignore
let emitter = new ee();

export const notify = (msg: string) => {
    emitter.emit('notification', msg)
}

interface Properties {

}

interface IState {
    msg: string
}

export default class Notifications extends React.Component<Properties, IState> {
    private timeout: any;


    constructor(props: any) {
        super(props);

        this.state = {
            msg: ''
        }

        this.timeout = null;
        emitter.on('notification', (msg: string) => {
            this.onShow(msg)
        })
    }

    onShow = (msg: string) => {
        if (this.timeout) {
            clearTimeout(this.timeout);
            this.setState({msg: ''}, () => {
                this.timeout = setTimeout(() => {
                    this.showNotification(msg)
                }, 200)
            })
        } else {
            this.showNotification(msg)
        }
    }


    showNotification = (msg: string) => {
        this.setState({
            msg: msg
        }, () => {
            this.timeout = setTimeout(() => {
                this.setState({msg: ''});
            }, 7000)
        });
    }


    render() {
        return (
            <React.Fragment>
                { this.state.msg != '' ?  <Container className={"custom-toast"} style={{position: 'absolute', left: '50vw', top: '13vh', transform: 'translate(-50%, -50%)', background: "#fa7566"}} top={10}> {this.state.msg} </Container> : null }
            </React.Fragment>
        )
    }
}
