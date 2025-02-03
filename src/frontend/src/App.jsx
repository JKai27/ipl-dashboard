import "./App.scss";
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import {TeamPage} from "./pages/TeamPage.jsx";
import {MatchPage} from "./pages/MatchPage.jsx";

function App() {
    return (
        <Router>
            <div className="App">
                <Routes>
                    <Route path="/teams/:teamName" element={<TeamPage/>}/>
                    <Route path="/teams/:teamName/matches/:year" element={<MatchPage/>}/>
                </Routes>

            </div>
        </Router>
    );
}

export default App;