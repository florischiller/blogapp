import React from "react";
import axios from "axios";
import 'bootstrap/dist/css/bootstrap.min.css';
import {Accordion} from "react-bootstrap";
import {ThreadItem, ThreadListItem} from "./ThreadListItem";

export type ThreadItemResponse = {
    id: string,
    name: string,
    userName: string,
    content: string,
    creationDate: string
}

export default class ListThreads extends React.Component {
    state = {
        isLoading: true,
        threads: [],
        error: ""
    };

    private static parseToModell(item: ThreadItemResponse): ThreadItem {
        return {
            id: item.id,
            name: item.name,
            userName: item.userName,
            content: item.content,
            creationDate: new Date(item.creationDate)
        };
    }

    componentDidMount() {
        this.loadUserThreads();
    }

    render() {
        const {isLoading, threads, error} = this.state;
        return (
            <Accordion>
                {error ? <p>{error}</p> : null}
                {!isLoading ? (threads.map(thread =>
                    ThreadListItem(thread)
                )) : (
                    <h3>Loading...</h3>
                )}
            </Accordion>
        );
    }

    private loadUserThreads() {
        axios.get<Array<ThreadItemResponse>>("/api/v1/threads").then(response => {
            // create a new "State" object without mutating
            // the original State object.
            this.setState({
                threads: response.data.map((item: ThreadItemResponse) => {
                        return ListThreads.parseToModell(item);
                    }
                ), isLoading: false
            });
        }).catch(error => {
            console.log(error);
            this.setState({error: error, isLoading: false});
        });
    }
}