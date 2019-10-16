import React from 'react';
import {BrowserRouter as Router, Link, Route, Switch} from 'react-router-dom';
import {Nav, Navbar} from 'react-bootstrap';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faHome, faUserLock} from "@fortawesome/free-solid-svg-icons";
import '../resources/css/App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Login from './login/Login'
import ThreadList from './threads/ListThreads'

function App() {
    return (
        <Router>
            <header className="header d-flex flex-grow-1 flex-column">
                <Navbar bg="dark" variant="dark" expand="sm">
                    <Navbar.Brand className={"justify-content-start"}>
                        <Nav.Link as={Link} to="/" className={"text-white"}>
                            <FontAwesomeIcon icon={faHome}/>
                        </Nav.Link>
                    </Navbar.Brand>
                    <Navbar.Collapse className={"justify-content-center flex-grow-1"}>
                        <Navbar.Text>
                            <h1 className="text-white mt-1">Thready.io</h1>
                        </Navbar.Text>
                    </Navbar.Collapse>
                    <Navbar className={"justify-content-end"}>
                        <Nav.Link as={Link} to="/login" className={"text-white"}>
                            <FontAwesomeIcon icon={faUserLock}/>
                        </Nav.Link>
                    </Navbar>
                </Navbar>
                <Switch>
                    <Route path="/login" component={Login}/>
                    <Route path="/" component={ThreadList}/>
                </Switch>
            </header>
        </Router>
    );
}

export default App;
