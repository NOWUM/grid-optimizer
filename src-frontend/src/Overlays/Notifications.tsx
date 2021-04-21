import React, { Component } from 'react';
import styled from 'styled-components';
import ee from "event-emitter"


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
    top: number,
    msg: string
}

export default class Notifications extends React.Component<Properties, IState> {
    private timeout: any;


    constructor(props: any) {
        super(props);

        this.state = {
            top: -100,
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
            this.setState({top: -100}, () => {
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
            top: 10,
            msg
        }, () => {
            this.timeout = setTimeout(() => {
                this.setState({top: -100});
            }, 3000)
        });
    }


    render() {
        return (
            <React.Fragment>
                <Container className={"custom-toast"} top={this.state.top}> {this.state.msg} </Container>
            </React.Fragment>
        )
    }
}
