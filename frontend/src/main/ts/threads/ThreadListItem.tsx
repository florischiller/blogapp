import React from "react";
import {Accordion, Card, Col, Row} from "react-bootstrap";
import ReactMarkdown from "react-markdown";

export type ThreadItem = {
    id: string,
    name: string,
    userName: string,
    content: string,
    creationDate: Date
}


export function ThreadListItem(item: ThreadItem) {
    return (
        <Card>
            <Card.Header>
                <Accordion.Toggle as={Card.Header} variant="link" eventKey={item.id}>
                    <Row>
                        <Col className={"col-6 justify-content-start text-left"}>{item.name}</Col>
                        <Col className={"col-3 justify-content-center text-right"}>By: {item.userName}</Col>
                        <Col className={"col-3 justify-content-end text-right"}>
                            Created
                            at: {item.creationDate.toLocaleTimeString()} on the {item.creationDate.toLocaleDateString()}
                        </Col>
                    </Row>
                </Accordion.Toggle>
            </Card.Header>
            <Accordion.Collapse eventKey={item.id}>
                <Card.Body>
                    <ReactMarkdown>
                        {item.content}
                    </ReactMarkdown>
                </Card.Body>
            </Accordion.Collapse>
        </Card>
    );
}